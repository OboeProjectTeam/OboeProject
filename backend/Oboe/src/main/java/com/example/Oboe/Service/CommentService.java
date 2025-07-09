package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, UserService userService, BlogRepository blogRepository) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.blogRepository = blogRepository;
    }


    public  Map<String, Object> getCommentsByTeamId(UUID teamId,int page ,int size) {
        // Lấy tất cả comment có referenceId = teamId (tức là liên quan đến đối tượng được bình luận)
        List<Comment> comments = commentRepository.findByReferenceId(teamId);

        // Chuyển đổi danh sách Entity thành danh sách DTOs
        List<CommentDTOs> allDtos = comments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        // Xây dựng cây comment cha–con
        List<CommentDTOs> rootComments = buildCommentTree(allDtos);
        long totalParent = rootComments.size();
        //phân trang cha
        List<CommentDTOs> paginated  = paginateComments(rootComments,page,size);
        Map<String,Object> response = new HashMap<>();
        response.put("comments", paginated);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalElements", totalParent);


        return response;
    }

    // chuyển từ Comment dạng phẳng sang dạng cây (cha - Con)
    private List<CommentDTOs> buildCommentTree(List<CommentDTOs> allDtos) {
        // Tạo một Map để tra cứu nhanh comment theo ID (dùng khi gắn comment con vào cha)
        Map<UUID, CommentDTOs> dtoMap = allDtos.stream()
                .collect(Collectors.toMap(CommentDTOs::getCommentId, dto -> dto));

        List<CommentDTOs> rootComments = new ArrayList<>();

        // Duyệt toàn bộ danh sách comment DTOs để phân loại cha – con
        for (CommentDTOs dto : allDtos) {
            UUID parentId = dto.getCommentIdParent();

            // Nếu không có parent → là comment cha (gốc)
            if (parentId == null) {
                rootComments.add(dto);
            } else {
                // Nếu có parent → gắn vào danh sách phản hồi (replies) của comment cha
                CommentDTOs parentDto = dtoMap.get(parentId);
                if (parentDto != null) {
                    parentDto.getReplies().add(dto);
                }
            }
        }
        // Trả về danh sách các comment cha đã được gắn đầy đủ phản hồi con
        return rootComments;
    }

     // phân trang về danh sách con của root comments theo phân trang
    private List<CommentDTOs> paginateComments(List<CommentDTOs> rootComments, int page, int size) {
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, rootComments.size());

        if (fromIndex > rootComments.size()) {
            return Collections.emptyList();
        }

        return rootComments.subList(fromIndex, toIndex);
    }

    //  Tạo comment mới (comment cha)
    public CommentDTOs createComment(UUID teamId, UUID userId, CommentDTOs dto) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng không hợp lệ");
        }

        // Kiểm tra blog (team) tồn tại không
        if (!blogRepository.existsById(teamId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog ID không tồn tại");
        }

        Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(userOpt.get());
        comment.setreferenceId(teamId);

        Comment saved = commentRepository.save(comment);
        return toDTO(saved);
    }

    // Tạo phản hồi (comment con) dựa trên comment cha
    public CommentDTOs Commentreply(UUID parentCommentId, UUID userId, CommentDTOs dto) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;

        Optional<Comment> parentOpt = commentRepository.findById(parentCommentId);
        if (parentOpt.isEmpty()) return null;

        Comment reply = new Comment();
        reply.setTitle(dto.getTitle());
        reply.setContent(dto.getContent());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUser(userOpt.get());
        reply.setParentComment(parentOpt.get());
        reply.setreferenceId(parentOpt.get().getreferenceId()); // Kế thừa blogId

        Comment saved = commentRepository.save(reply);
        return toDTO(saved);
    }

    //  Cập nhật comment (chỉ người tạo mới được sửa)
    public CommentDTOs updateComment(UUID commentId, UUID userId, CommentDTOs dto) {
        Comment comment = getCommentEntityById(commentId);
        if (comment == null) return null;

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;

        // Chỉ cho phép sửa nếu là người tạo
        if (!comment.getUser().getUser_id().equals(userOpt.get().getUser_id())) return null;

        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        return toDTO(commentRepository.save(comment));
    }

    //  Xóa comment nếu đúng người tạo
    public boolean deleteComment(UUID commentId, UUID userId) {
        Comment comment = getCommentEntityById(commentId);
        if (comment == null) return false;

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return false;

        if (!comment.getUser().getUser_id().equals(userOpt.get().getUser_id())) return false;

        commentRepository.deleteById(commentId);
        return true;
    }

    //  Lấy tất cả comment của một user
    public List<CommentDTOs> getCommentByUserId(UUID userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;

        List<Comment> comments = commentRepository.findCommentByUserId(userId);
        return comments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //  Lấy số lượng comment theo teamId (blogId)
    public Long getCommentCountByTeamId(UUID teamId) {
        return commentRepository.countByReferenceId(teamId);
    }

    //  Hàm dùng chung để lấy comment theo ID
    public Comment getCommentEntityById(UUID commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    //  Chuyển từ entity -> DTO
    private CommentDTOs toDTO(Comment comment) {
        CommentDTOs dto = new CommentDTOs();
        dto.setCommentId(comment.getCommentId());
        dto.setTitle(comment.getTitle());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());

        // Gán thông tin người dùng
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getUser_id());
            dto.setUserName(comment.getUser().getUserName());
        }

        // Nếu là phản hồi thì set comment cha
        if (comment.getParentComment() != null) {
            dto.setCommentIdParent(comment.getParentComment().getCommentId());
        }

        dto.setReferenceId(comment.getreferenceId());
        dto.setReplies(new ArrayList<>()); // Khởi tạo danh sách phản hồi
        return dto;
    }
}
