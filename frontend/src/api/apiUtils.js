import axios from './axiosConfig';

/**
 * Xử lý các lỗi từ API và trả về thông báo lỗi phù hợp
 * @param {Error} error - Đối tượng lỗi từ axios
 * @returns {string} Thông báo lỗi đã được format
 */
export const handleApiError = (error) => {
  let errorMessage = 'Đã xảy ra lỗi. Vui lòng thử lại sau.';
  
  if (error.response) {
    // Lỗi từ phía server
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        errorMessage = data.message || 'Yêu cầu không hợp lệ';
        break;
      case 401:
        errorMessage = 'Phiên đăng nhập đã hết hạn';
        break;
      case 403:
        errorMessage = 'Bạn không có quyền thực hiện thao tác này';
        break;
      case 404:
        errorMessage = 'Không tìm thấy dữ liệu yêu cầu';
        break;
      case 422:
        errorMessage = data.message || 'Dữ liệu không hợp lệ';
        break;
      case 500:
        errorMessage = 'Lỗi hệ thống. Vui lòng thử lại sau';
        break;
      default:
        errorMessage = data.message || errorMessage;
    }
  } else if (error.request) {
    // Lỗi không nhận được response
    errorMessage = 'Không thể kết nối đến máy chủ';
  }

  return errorMessage;
};

/**
 * Upload file lên server
 * @param {File} file - File cần upload
 * @param {string} type - Loại file (mặc định là 'image')
 * @returns {Promise} Kết quả upload từ server
 */
export const uploadFile = async (file, type = 'image') => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);

    const response = await axios.post('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

/**
 * Tạo chuỗi query từ object params
 * Ví dụ: { page: 1, limit: 10 } => "page=1&limit=10"
 * @param {Object} params - Object chứa các tham số
 * @returns {string} Chuỗi query đã được format
 */
export const buildQueryString = (params) => {
  const query = new URLSearchParams();
  
  Object.entries(params).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== '') {
      if (Array.isArray(value)) {
        value.forEach(item => query.append(key, item));
      } else {
        query.append(key, value);
      }
    }
  });
  
  return query.toString();
};

/**
 * Tạo object params cho phân trang
 * @param {number} page - Số trang hiện tại
 * @param {number} limit - Số lượng item trên mỗi trang
 * @returns {Object} Object chứa thông tin phân trang
 */
export const getPaginationParams = (page = 1, limit = 10) => ({
  page,
  limit,
}); 