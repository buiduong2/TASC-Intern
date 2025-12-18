import { environment } from '../../environments/environment';
import type { AccessTokenBody } from '../../types/oauth.type';

function dec2hex(dec: any) {
  return ('0' + dec.toString(16)).substring(-2);
}

function sha256(plain: any) {
  const encoder = new TextEncoder();
  const data = encoder.encode(plain);
  return window.crypto.subtle.digest('SHA-256', data);
}

function base64urlencode(a: any) {
  var str = '';
  var bytes = new Uint8Array(a);
  var len = bytes.byteLength;
  for (var i = 0; i < len; i++) {
    str += String.fromCharCode(bytes[i]);
  }
  return btoa(str).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

function generateCodeVerifier() {
  var array = new Uint32Array(56 / 2);
  window.crypto.getRandomValues(array);
  return Array.from(array, dec2hex).join('');
}

async function generateCodeChallengeFromVerifier(v: any) {
  var hashed = await sha256(v);
  var base64encoded = base64urlencode(hashed);
  return base64encoded;
}

export async function generatePKCE(): Promise<{
  verifier: string;
  challenge: string;
}> {
  const verifier: string = generateCodeVerifier();
  const challenge: string = await generateCodeChallengeFromVerifier(verifier);
  return {
    challenge,
    verifier,
  };
}

export const oauthConfig = {
  authUrl: environment.authUrl,
  client_id: environment.oauthClientId,
  client_secret: environment.oauthClientSecret,
  code_challenge_method: 'S256',
  redirect_uri: environment.oauthCallback,
  logout_uri: environment.oauthLogoutUri,
  scope: 'openid',
};

export function generateOAuthUrl(challenge: string): string {
  const { authUrl, client_id, code_challenge_method, redirect_uri, scope } = oauthConfig;
  return `${authUrl}/oauth2/authorize?response_type=code&client_id=${client_id}&scope=${scope}&redirect_uri=${redirect_uri}&code_challenge=${challenge}&code_challenge_method=${code_challenge_method}`;
}

export function generateOAuthChangePasswordUrl(): string {
  return `${oauthConfig.authUrl}/change-password`;
}

export function generateOAuthProfileUrl(): string {
  return `${oauthConfig.authUrl}/profile`;
}

export function generateOAuthPermissionsUrl(): string {
  return `${oauthConfig.authUrl}/permissions`;
}

export function generateOauthLogoutUrl(tokenId: string) {
  return (
    oauthConfig.authUrl +
    '/connect/logout?id_token_hint=' +
    tokenId +
    '&post_logout_redirect_uri=' +
    oauthConfig.logout_uri
  );
}

export function parseAccessToken(rawAccessToken: string): AccessTokenBody {
  return JSON.parse(atob(rawAccessToken.split('.')[1])) as AccessTokenBody;
}

export function isAccessTokenExpired(accessToken: AccessTokenBody, thresHoldMs: number) {
  const exp = accessToken.exp;
  const now = new Date().getTime();
  return now > exp + thresHoldMs;
}

export function saveCode(code: string) {
  localStorage.setItem('code', code);
}

export function getCode(): string | null {
  const code = localStorage.getItem('code');

  if (code != null) {
    localStorage.removeItem('code');
  }
  return code;
}

export function getClientHeaders() {
  return {
    Authorization: 'Basic ' + btoa(`${oauthConfig.client_id}:${oauthConfig.client_secret}`),
  };
}

export function openOAuthPopup(url: string): ReturnType<Window['open']> {
  return window.open(url, 'OAuth2', 'width=500,height=600');
}
