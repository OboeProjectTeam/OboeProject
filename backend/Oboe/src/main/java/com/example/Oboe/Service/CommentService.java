package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.Notifications;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Repository.NotificationsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final NotificationsRepository notificationsRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public CommentService(CommentRepository commentRepository, UserService userService, BlogRepository blogRepository, NotificationsRepository notificationsRepository, SimpMessagingTemplate messagingTemplate) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.blogRepository = blogRepository;
        this.notificationsRepository = notificationsRepository;
        this.messagingTemplate = messagingTemplate;
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
        User sender = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng không hợp lệ"));

        // Lấy blog theo ID
        Blog blog = blogRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog ID không tồn tại"));

        // Tạo Comment
        Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(sender);
        comment.setreferenceId(teamId);

        Comment saved = commentRepository.save(comment);

        // Gửi WebSocket: Bình luận mới
        CommentDTOs commentDTO = toDTO(saved);
        messagingTemplate.convertAndSend(
                "/blog/" + blog.getBlogId() + "/comments",
                commentDTO
        );



        User receiver = blog.getUser();

        Notifications notification = new Notifications();
        notification.setUser(receiver);
        notification.setText_notification("Bạn vừa nhận được một bình luận mới từ " + sender.getUserName());
        notification.setRead(false);
        notification.setUpdate_at(LocalDateTime.now());

        Notifications savedNoti = notificationsRepository.save(notification);

        // Gửi WebSocket thông báo cho chủ blog
        messagingTemplate.convertAndSend(
                "/notification/" + receiver.getUser_id(),
                savedNoti.getText_notification()

        );

        return commentDTO;

    }

    // Tạo phản hồi (comment con) dựa trên comment cha
    public CommentDTOs Commentreply(UUID parentCommentId, UUID userId, CommentDTOs dto) {
        //  Lấy người gửi
        User sender = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng không hợp lệ"));

        //  Lấy comment cha
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận cha"));

        //  Tạo phản hồi (comment con)
        Comment reply = new Comment();
        reply.setTitle(dto.getTitle());
        reply.setContent(dto.getContent());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUser(sender);
        reply.setParentComment(parent);
        reply.setreferenceId(parent.getreferenceId()); // blogId

        Comment savedReply = commentRepository.save(reply);

        //  Gửi thông báo nếu người nhận khác người gửi
        User receiver = parent.getUser();

            Notifications notification = new Notifications();
            notification.setUser(receiver);
            notification.setText_notification("Bạn vừa nhận được một phản hồi từ " + sender.getUserName());
            notification.setRead(false);
            notification.setUpdate_at(LocalDateTime.now());

            Notifications savedNoti = notificationsRepository.save(notification);

            // Gửi WebSocket thông báo riêng cho Comment cha
            messagingTemplate.convertAndSend(
                    "/notification/" + receiver.getUser_id(),
                    savedNoti.getText_notification()
            );


        return toDTO(savedReply);
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
