package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.Notifications;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final NotificationsRepository notificationsRepository;
    private final KanjiRepository kanjiRepository;
    private final GrammarRepository grammarRepository;
    private final SampleSentenceRepository sampleSentenceRepository ;
    private final VocabularyRepository vocabularyRepository ;


    public CommentService(CommentRepository commentRepository, UserService userService, BlogRepository blogRepository, NotificationsRepository notificationsRepository,
                          KanjiRepository kanjiRepository,
                          GrammarRepository grammarRepository,
                          VocabularyRepository vocabularyRepository
                          ,
                          SampleSentenceRepository sampleSentenceRepository) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.blogRepository = blogRepository;
        this.notificationsRepository = notificationsRepository;
        this.kanjiRepository = kanjiRepository;
        this.grammarRepository = grammarRepository;
        this.sampleSentenceRepository = sampleSentenceRepository;
        this.vocabularyRepository = vocabularyRepository;


    }


    public Map<String, Object> getCommentsByTeamId(UUID teamId, int page, int size) {
        List<Comment> comments = commentRepository.findByReferenceId(teamId);

        List<CommentDTOs> allDtos = comments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        //  Tạo cây và lọc ra các comment cha (gốc)
        List<CommentDTOs> commentTree = buildCommentTree(allDtos);

        //  commentTree là danh sách gốc (cha), vì buildCommentTree đã lọc
        List<CommentDTOs> rootComments = commentTree;

        //  Phân trang theo comment cha
        List<CommentDTOs> paginated = paginateComments(rootComments, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("comments", paginated);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalElements", allDtos.size());
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
        //  Lấy người gửi
        User sender = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng không hợp lệ"));

        User receiver = null;
        boolean isBlog = false;

        //  Xác định loại nội dung
        Optional<Blog> blogOpt = blogRepository.findById(teamId);
        if (blogOpt.isPresent()) {
            isBlog = true;
            receiver = blogOpt.get().getUser();
        } else if (
                !kanjiRepository.existsById(teamId) &&
                        !grammarRepository.existsById(teamId) &&
                     !sampleSentenceRepository.existsById(teamId)&&
                        !vocabularyRepository.existsById(teamId)
        ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nội dung phù hợp để bình luận");
        }

        //  Lấy giờ Việt Nam
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime vietnamTime = LocalDateTime.now(vietnamZone);

        //  Tạo Comment
        Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(vietnamTime);
        comment.setUser(sender);
        comment.setreferenceId(teamId);
        Comment saved = commentRepository.save(comment);
        CommentDTOs commentDTO = toDTO(saved);

        //  Nếu là comment blog → tạo thông báo và gửi WebSocket
        // Nếu là comment blog → tạo thông báo và gửi WebSocket
        if (isBlog && receiver != null && !receiver.getUser_id().equals(sender.getUser_id())) {
            // Tạo thông báo
            Notifications notification = new Notifications();
            notification.setUser(receiver);
            notification.setText_notification("Bạn vừa nhận được một bình luận mới từ " + sender.getUserName());
            notification.setRead(false);
            notification.setUpdate_at(vietnamTime);
            notification.setTargetId(saved.getreferenceId());          // ID của comment
            notification.setTargetType("BLog");            // Kiểu: "Comment"

            Notifications savedNoti = notificationsRepository.save(notification);

            // Gửi WebSocket nếu người nhận đang online
            WebSocketSession receiverSession = SessionManager.getSession(receiver.getUser_id());

            if (receiverSession != null && receiverSession.isOpen()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);

                    String commentJson = mapper.writeValueAsString(commentDTO);
                    String notificationJson = mapper.writeValueAsString(savedNoti);

                    // Gửi cả comment và notification
                    receiverSession.sendMessage(new TextMessage(commentJson));
                    receiverSession.sendMessage(new TextMessage(notificationJson));

                } catch (IOException e) {
                    e.printStackTrace(); // In ra thông tin lỗi
                }
            }
        }
        return commentDTO;
    }



    // Tạo phản hồi (comment con) dựa trên comment cha
    public CommentDTOs Commentreply(UUID parentCommentId, UUID userId, CommentDTOs dto) {
        // Lấy người gửi (sender) từ userId
        User sender = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng không hợp lệ"));

        // Lấy comment cha (parent comment)
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận cha"));

        // Lấy giờ hiện tại theo múi giờ Việt Nam
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime vietnamTime = LocalDateTime.now(vietnamZone);

        // Tạo comment con (reply)
        Comment reply = new Comment();
        reply.setTitle(dto.getTitle());
        reply.setContent(dto.getContent());
        reply.setCreatedAt(vietnamTime);
        reply.setUser(sender);
        reply.setParentComment(parent);
        reply.setreferenceId(parent.getreferenceId()); // giữ nguyên blogId hoặc teamId giống comment cha

        // Lưu comment con vào database
        Comment savedReply = commentRepository.save(reply);

        // Lấy người nhận (receiver) là người viết comment cha
        User receiver = parent.getUser();

        // Nếu người nhận khác người gửi → tạo và lưu thông báo
        if (receiver != null && !receiver.getUser_id().equals(sender.getUser_id())) {
            Notifications notification = new Notifications();
            notification.setUser(receiver); // Gửi tới người nhận
            notification.setText_notification("Bạn vừa nhận được một phản hồi từ " + sender.getUserName());
            notification.setRead(false);
            notification.setUpdate_at(vietnamTime);
            notification.setTargetId(parent.getreferenceId());
            notification.setTargetType("BLog");

            Notifications savedNoti = notificationsRepository.save(notification);

            // Gửi WebSocket nếu người nhận đang online
            WebSocketSession receiverSession = SessionManager.getSession(receiver.getUser_id());
            if (receiverSession != null && receiverSession.isOpen()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);

                    // JSON của bình luận phản hồi
                    String replyJson = mapper.writeValueAsString(toDTO(savedReply));
                    // JSON của thông báo
                    String notificationJson = mapper.writeValueAsString(savedNoti);

                    receiverSession.sendMessage(new TextMessage(replyJson));
                    receiverSession.sendMessage(new TextMessage(notificationJson));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return toDTO(savedReply);
    }
    public CommentDTOs getcommentbyID(UUID commentId) {
        Comment comment = getCommentEntityById(commentId);
        if (comment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận với ID đã cho");
        }
        return toDTO(comment);
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

    // Lấy tất cả comment của một user
    public List<CommentDTOs> getCommentByUserId(UUID userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of(); // trả về List rỗng nếu không có user
        List<Comment> comments = commentRepository.findCommentByUserId(userId);
        return comments.stream()
                .map(this::toShortDTO).toList();
    }

    //lấy  Lấy tất cả comment của một user
    public Page<CommentDTOs> getCommentByUserIds(UUID userId, int page, int size) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return Page.empty(); // Trả về Page rỗng thay vì List.of()
        }
        Pageable pageable = PageRequest.of(page, size); //  Tạo pageable
        Page<Comment> commentPage = commentRepository.findCommentByUserIds(userId, pageable); //

        List<CommentDTOs> dtoList = commentPage.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, commentPage.getTotalElements()); // Tạo Page<CommentDTOs>
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
            dto.setAvatarUrl(comment.getUser().getAvatarUrl());
        }
        // Nếu là phản hồi thì set comment cha
        if (comment.getParentComment() != null) {
            dto.setCommentIdParent(comment.getParentComment().getCommentId());
        }
        dto.setReferenceId(comment.getreferenceId());
        dto.setReplies(new ArrayList<>()); // Khởi tạo danh sách phản hồi
        return dto;
    }
    private CommentDTOs toShortDTO(Comment comment) {
        CommentDTOs dto = new CommentDTOs();
        dto.setCommentId(comment.getCommentId());
        dto.setTitle(comment.getTitle());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getUser_id());
            dto.setUserName(comment.getUser().getUserName());
        }
        if (comment.getParentComment() != null) {
            dto.setCommentIdParent(comment.getParentComment().getCommentId());
        }
        dto.setReferenceId(comment.getreferenceId());
        return dto;
    }

}
