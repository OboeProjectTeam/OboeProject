import { computed, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { useGlobalPremiumSync } from './useGlobalPremiumSync'

export function usePremiumCheck() {
  const store = useStore()
  const router = useRouter()
  const { requestPremiumSync } = useGlobalPremiumSync()
  
  const currentUser = computed(() => store.getters['auth/currentUser'])
  
  const isPremium = computed(() => {
    return currentUser.value?.accountType === 'PREMIUM'
  })
  
  // Popup state
  const showPremiumPopup = ref(false)
  const premiumPopupMessage = ref('')
  const premiumPopupTitle = ref('Tính năng Premium')
  
  const showPremiumRequiredPopup = () => {
    premiumPopupTitle.value = '🌟 Tính năng Premium'
    premiumPopupMessage.value = `Tính năng này chỉ dành cho thành viên Premium.

• Truy cập không giới hạn tất cả tính năng AI
• Tự động tạo câu hỏi và flashcard
• Hỗ trợ dịch thuật thông minh
• Phân tích học tập chi tiết

Nâng cấp ngay để trải nghiệm đầy đủ!`
    showPremiumPopup.value = true
  }
  
  const handlePremiumPopupConfirm = () => {
    showPremiumPopup.value = false
    // Chuyển hướng đến trang thanh toán
    router.push('/payment')
  }
  
  const handlePremiumPopupCancel = () => {
    showPremiumPopup.value = false
  }
  
  const checkPremiumFeature = async () => {
    // Sync premium status trước khi kiểm tra
    try {
      await requestPremiumSync()
    } catch (error) {
      console.error('Error syncing premium status:', error)
      // Vẫn tiếp tục kiểm tra với dữ liệu hiện tại
    }
    
    if (!isPremium.value) {
      showPremiumRequiredPopup()
      return false
    }
    return true
  }
  
  return {
    isPremium,
    currentUser,
    showPremiumPopup,
    premiumPopupMessage,
    premiumPopupTitle,
    showPremiumRequiredPopup,
    handlePremiumPopupConfirm,
    handlePremiumPopupCancel,
    checkPremiumFeature
  }
}