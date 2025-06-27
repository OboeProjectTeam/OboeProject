package com.example.Oboe.Service;

import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.DTOs.KanjiDTO;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.Kanji;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Repository.KanjiRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BlogService blogService;
    private final KanjiRepository kanjiRepository;

    public CommentService(CommentRepository commentRepository, UserService userService, BlogService blogService, KanjiRepository kanjiRepository) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.blogService = blogService;
        this.kanjiRepository = kanjiRepository;

    }

    public List<CommentDTOs> getCommentsByBlogId(UUID blogId) {
        return commentRepository.findByBlog_BlogId(blogId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public List<CommentDTOs> getCommentsByBlogIdFull(UUID blogId) {
        //Truy vấn tất cả comment trong cùng Blog ,Truy vấn tất cả các Comment thuộc blog có blogId.
        List<Comment> comments = commentRepository.findByBlog_BlogId(blogId);
        //Covert sang Dto,Chuyển toàn bộ Comment sang CommentDTOs bằng hàm Dtos
        List<CommentDTOs> AllDtos = comments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        // Map CommentId sang Dto
        Map<UUID, CommentDTOs> dtoMap = AllDtos.stream()
                .collect(Collectors.toMap(CommentDTOs::getCommentId, dto -> dto));
        //Duyệt để gán Replies vào comment cha ,danh sách chứa các comment (không có cha)
        List<CommentDTOs>  rootCommentParent = new ArrayList<>();
        for (CommentDTOs Dtos : AllDtos) {
            UUID CommentParent = Dtos.getCommentIdParent();
            //nếu có null là comment Cha và ngược lại là comment con
            if(CommentParent == null){
                rootCommentParent.add(Dtos);
            }
            else{
                CommentDTOs DtosParent = dtoMap.get(CommentParent);
                if(DtosParent != null){
                    //comment con
                    DtosParent.getReplies().add(Dtos);
                }
            }
        }
        return rootCommentParent;
    }
    public CommentDTOs getCommentDTOById(UUID commentId) {
        return commentRepository.findById(commentId).map(this::toDTO).orElse(null);
    }

    public List<CommentDTOs> getCommentsByUsername(String username) {
        Optional<User> Tennguoidung = userService.findByUserName(username);
        if (Tennguoidung.isEmpty()) return Collections.emptyList();
        return commentRepository.findCommentByCommentId(Tennguoidung.get().getUser_id())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Comment getCommentEntityById(UUID commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    public CommentDTOs createComment(UUID blogId, String username, CommentDTOs dto) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return null;

        Blog blog = blogService.getBlogById(blogId);
        if (blog == null ) return null;

        Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(userOpt.get());
        comment.setBlog(blog);
        comment.setKanji(null);

        Comment saved = commentRepository.save(comment);
        return toDTO(saved);
    }
    public CommentDTOs createCommentKanji(UUID kanjiID, String username, CommentDTOs dto) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return null;

        // ✅ Dùng ID để lấy entity Kanji
        Optional<Kanji> kanjiOpt = kanjiRepository.findById(kanjiID);
        if (kanjiOpt.isEmpty()) return null;

        Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(userOpt.get());
        comment.setKanji(kanjiOpt.get());
        comment.setBlog(null);

        Comment saved = commentRepository.save(comment);
        return toDTO(saved);
    }

    public CommentDTOs Commentreply(UUID ReplyCommentId, String username, CommentDTOs dto) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return null;

        Optional<Comment> commentOpt = commentRepository.findById(ReplyCommentId);
        if (commentOpt.isEmpty()) return null;

        //Lấy BlogId qua  comment Cha
        Comment commentParent = commentOpt.get();
        Blog blog = commentParent.getBlog();

        Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(userOpt.get());
        comment.setBlog(blog);
        comment.setParentComment(commentOpt.get());
        commentRepository.save(comment);
        return toDTO(comment);
    }

    public CommentDTOs updateComment(UUID commentId, String username, CommentDTOs dto) {
        Comment comment = getCommentEntityById(commentId);
        if (comment == null) return null;

        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return null;

        if (!comment.getUser().getUser_id().equals(userOpt.get().getUser_id())) return null;

        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);
        return toDTO(updated);
    }

    public boolean deleteComment(UUID commentId, String username) {
        Comment comment = getCommentEntityById(commentId);
        if (comment == null) return false;

        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return false;

        if (!comment.getUser().getUser_id().equals(userOpt.get().getUser_id())) return false;

        commentRepository.deleteById(commentId);
        return true;
    }


    public Long getCommentCountByBlogId(UUID blogId) {
        return commentRepository.countByBlogId(blogId);
    }


    private CommentDTOs toDTO(Comment comment) {
        CommentDTOs dto = new CommentDTOs();
        dto.setCommentId(comment.getCommentId());
        dto.setTitle(comment.getTitle());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());

        // Set nếu là reply (comment có cha)
        if (comment.getParentComment() != null) {
            dto.setCommentIdParent(comment.getParentComment().getCommentId());
        }
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getUser_id());
            dto.setUserName(comment.getUser().getUserName());
        }

        if (comment.getBlog() != null) {
            dto.setBlogId(comment.getBlog().getBlogId());
            dto.setBlogTitle(comment.getBlog().getTitle());
        }
        dto.setReplies(new ArrayList<>());
        return dto;
    }
}
