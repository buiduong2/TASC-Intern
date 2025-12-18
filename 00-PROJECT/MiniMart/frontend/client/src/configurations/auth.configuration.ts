// auth.providers.ts (Tạo file mới)

import { Provider, APP_INITIALIZER } from '@angular/core';
import SuperTokensLock from 'browser-tabs-lock';

import { Profile } from '../repositories/auth.repository';
import { TokenRes } from '../types/oauth.type';
import { AuthService } from '../services/auth.service';
import {
  profileSuplier,
  ProfileSuplier,
  tokenExchanger,
  TokenExchanger,
  tokenSuplier,
  TokenSuplier,
} from '../utils/auth/auth-suplier.utils';

/**
 * Hàm khởi tạo (Factory) để chạy AuthService.intializeProfile() khi ứng dụng khởi động.
 */
function initializeAuthFactory(authService: AuthService) {
  return () =>
    authService.intializeProfile().catch(() => {
      console.warn('Authentication initialization failed. Starting app unauthenticated.');
    });
}

/**
 * Cung cấp tất cả các providers cần thiết cho hệ thống Xác thực (Auth).
 * @param config Cấu hình chi tiết API và ngưỡng thời gian (threshold).
 */
export function provideAuth(config: { tokenTtlThresholdMs: number }): Provider[] {
  return [
    {
      provide: 'DISTRIBUTED_LOCK_INSTANCE',
      useValue: new SuperTokensLock(), // Cung cấp instance của thư viện Lock
    },
    {
      provide: 'TOKEN_TTL_THRESHOLD',
      useValue: config.tokenTtlThresholdMs,
    },
    {
      provide: 'PROFILE_SUPLIER',
      useValue: profileSuplier,
    },
    {
      provide: 'TOKEN_SUPLIER',
      useValue: tokenSuplier,
    },
    {
      provide: 'TOKEN_EXCHANGER',
      useValue: tokenExchanger,
    },

    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuthFactory,
      deps: [AuthService],
      multi: true,
    },
  ];
}
