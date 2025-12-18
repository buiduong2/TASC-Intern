type LockOption = { ttl: number };
type Lock = { release: () => void };

export async function acquireLock(key: string, { ttl: ttlMs }: LockOption): Promise<Lock> {
  const lockInstance: Lock = {
    release: () => {
      localStorage.removeItem(key);
    },
  };

  // --- BƯỚC 1: THỬ GIÀNH KHÓA BAN ĐẦU ---
  // Kiểm tra và Set khóa (SetNx)
  const lockSetted = localstorageSetNx(key, ttlMs);

  if (lockSetted) {
    return Promise.resolve(lockInstance);
  }

  // --- BƯỚC 2: CHỜ VÀ THỬ LẠI (WAIT AND RETRY) ---
  // Khóa đang bị giữ, thiết lập cơ chế chờ
  return new Promise((res, rej) => {
    let storageListenerActive = true;

    function handleStorageEvent(ev: StorageEvent) {
      // Chỉ quan tâm đến khóa của mình
      if (ev.key === key && ev.newValue === null) {
        // Khóa đã bị giải phóng, thử giành khóa lại (RETRY)

        // Vô hiệu hóa listener hiện tại để tránh lỗi re-entry
        window.removeEventListener('storage', handleStorageEvent);
        storageListenerActive = false;
        clearTimeout(timeoutId); // Xóa timeout cho lần chờ này

        // LUI VÀO HÀNG ĐỢI (Đảm bảo tất cả các tab đã xử lý event)
        setTimeout(async () => {
          // Thử giành khóa lại
          const retryLockSetted = localstorageSetNx(key, ttlMs);

          if (retryLockSetted) {
            res(lockInstance); // Thành công!
          } else {
            // Thất bại lần 2 (Tab khác đã giành khóa)
            // Bắt đầu chờ lại. Đây là điểm logic phức tạp của Polling.
            // (Để đơn giản, ta chỉ rej hoặc recursive gọi lại acquireLock).
            rej(new Error('Lock contention failed after release.'));
          }
        }, 50); // Chờ 50ms để giảm race condition giữa các tab đang chờ
      }
    }

    const timeoutId = setTimeout(() => {
      if (storageListenerActive) {
        window.removeEventListener('storage', handleStorageEvent);
      }
      rej(new Error('Lock acquisition timed out.'));
    }, 5000);

    window.addEventListener('storage', handleStorageEvent);
  });
}
