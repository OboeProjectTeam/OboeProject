import { ref, watch } from 'vue'
import { useStore } from 'vuex'
import profileApi from '@/api/modules/profileApi'

// Global state để theo dõi việc sync premium
const isSyncing = ref(false)
const lastSyncTime = ref(0)

export function useGlobalPremiumSync() {
  const store = useStore()
  
  // Hàm sync profile từ server
  const syncPremiumStatus = async (force = false) => {
    // Tránh sync quá thường xuyên (tối thiểu 5 giây giữa các lần sync)
    const now = Date.now()
    if (!force && (now - lastSyncTime.value) < 5000) {
      return
    }
    
    if (isSyncing.value) {
      return // Đang sync rồi thì không sync nữa
    }
    
    try {
      isSyncing.value = true
      lastSyncTime.value = now
      
      // Gọi API để lấy profile mới nhất
      const profile = await profileApi.getProfile()
      
      // Cập nhật store auth với thông tin mới
      store.commit('auth/SET_USER', profile)
      
      return profile
      
    } catch (error) {
      console.error('Error syncing premium status:', error)
      throw error
    } finally {
      isSyncing.value = false
    }
  }
  
  // Hàm để các component khác gọi khi cần sync
  const requestPremiumSync = async () => {
    return await syncPremiumStatus(true)
  }
  
  // Auto sync khi có thay đổi trong localStorage (từ tab khác)
  const handleStorageChange = (e) => {
    if (e.key === 'user' && e.newValue) {
      try {
        const newUser = JSON.parse(e.newValue)
        const currentUser = store.getters['auth/currentUser']
        
        // Nếu accountType thay đổi thì sync lại
        if (currentUser?.accountType !== newUser.accountType) {
          syncPremiumStatus(true)
        }
      } catch (error) {
        console.error('Error parsing user from localStorage:', error)
      }
    }
  }
  
  // Listen storage changes
  if (typeof window !== 'undefined') {
    window.addEventListener('storage', handleStorageChange)
  }
  
  return {
    isSyncing,
    syncPremiumStatus,
    requestPremiumSync
  }
}