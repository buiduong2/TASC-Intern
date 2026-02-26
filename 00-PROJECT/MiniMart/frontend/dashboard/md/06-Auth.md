### Kiến trúc


```
src/app/
│
├── core/
│   ├── guards/
│   ├── interceptors/
│   ├── services/
│   └── models/
│
├── features/
│   ├── auth/
│   │   ├── pages/
│   │   │   ├── login/
│   │   │   │   ├── login.component.ts
│   │   │   │   ├── login.component.html
│   │   │   │   └── login.component.scss
│   │   │   │
│   │   │   └── register/ (optional)
│   │   │
│   │   ├── components/ (reusable UI part)
│   │   ├── services/
│   │   │   └── auth.service.ts
│   │   ├── models/
│   │   │   ├── login-request.model.ts
│   │   │   └── user.model.ts
│   │   ├── auth.routes.ts
│   │   └── auth.module.ts (nếu dùng module-based)
│   │
│   └── dashboard/
│
└── shared/
```

### Login Page:

- Quyết định sử dụng full page redirect
- Authorization Code + PKCE
- 

### Flow

- Click Login
- Redirect sang Auth Server
- Login
- Redirect Về app
