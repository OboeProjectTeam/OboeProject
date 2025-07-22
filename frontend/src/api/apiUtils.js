/**
 * Trích xuất thông điệp lỗi dễ đọc từ axios error
 * @param {Error} error - Axios error object
 * @returns {string} - Thông báo lỗi phù hợp để hiển thị
 */
export function handleApiError(error) {
    if (error.response) {
      const { status, data } = error.response;
  
      if (typeof data === 'string') {
        return data; // Trường hợp backend trả chuỗi thông báo lỗi
      }
  
      if (data?.message) {
        return data.message;
      }
  
      // Trường hợp không có message cụ thể
      switch (status) {
        case 401:
          return 'Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn.';
        case 403:
          return 'Bạn không có quyền thực hiện hành động này.';
        case 404:
          return 'Không tìm thấy tài nguyên.';
        default:
          if (status >= 500) return 'Lỗi máy chủ. Vui lòng thử lại sau.';
          return 'Đã xảy ra lỗi. Vui lòng thử lại.';
      }
    }
  
    if (error.request) {
      return 'Không thể kết nối đến máy chủ. Kiểm tra mạng hoặc thử lại sau.';
    }
  
    return error.message || 'Lỗi không xác định';
  }
  