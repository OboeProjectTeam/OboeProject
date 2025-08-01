import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/ai-reply';

const aiReplyApi = {
  // Gửi yêu cầu tạo phản hồi AI cho bài viết (blog)
  async replyToBlog(blogId) {
    try {
      const res = await axios.post(`${PREFIX}/blog/${blogId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Gửi yêu cầu tạo phản hồi AI cho bình luận (comment)
  async replyToComment(commentId) {
    try {
      const res = await axios.post(`${PREFIX}/comment/${commentId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default aiReplyApi;
