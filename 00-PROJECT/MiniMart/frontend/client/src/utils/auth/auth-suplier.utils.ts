import { environment } from '../../environments/environment';
import { Profile } from '../../repositories/auth.repository';
import { TokenRes } from '../../types/oauth.type';
import { getClientHeaders } from './oauth.utils';

export type ProfileSuplier = (accessToken: string) => Promise<Profile>;
export type TokenExchanger = (code: string, verifier: string) => Promise<TokenRes>;
export type TokenSuplier = (refreshToken: string) => Promise<TokenRes>;

const authUrl: string = environment.authUrl;
const apiUrl:string = environment.apiUrl;

const PROFILE_URL = `${apiUrl}/v1/auth/me`;
export const profileSuplier: ProfileSuplier = async (accessToken: string): Promise<Profile> => {
  const res = await fetch(PROFILE_URL, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
  });

  if (res.ok) {
    return res.json() as Promise<Profile>;
  }

  console.error('Failed to fetch user profile:', res.status, res.statusText);
  throw new Error(`Failed to fetch user profile: ${res.status}`);
};

export const tokenExchanger: TokenExchanger = fetchAccessToken;

export const tokenSuplier: TokenSuplier = fetchRefreshAccessToken;

export const oauthConfig = {
  authUrl: environment.apiUrl,
  client_id: environment.oauthClientId,
  client_secret: environment.oauthClientSecret,
  code_challenge_method: 'S256',
  redirect_uri: environment.oauthCallback,
  logout_uri: environment.oauthLogoutUri,
  scope: 'openid',
};

async function fetchAccessToken(code: string, verifier: string): Promise<TokenRes> {
  const formData = new FormData();
  formData.append('code', code);
  formData.append('grant_type', 'authorization_code');

  formData.append('redirect_uri', oauthConfig.redirect_uri);
  formData.append('code_verifier', verifier);

  return fetchClientOAuth(oauthConfig.authUrl + '/oauth2/token', formData);
}

function fetchRefreshAccessToken(refreshToken: string): Promise<TokenRes> {
  const formData = new FormData();
  formData.append('grant_type', 'refresh_token');
  formData.append('refresh_token', refreshToken);

  return fetchClientOAuth(oauthConfig.authUrl + '/oauth2/token', formData);
}

async function revokeRefreshToken(refreshToken: string) {
  const formData = new FormData();
  formData.append('token', refreshToken);
  return fetchClientOAuth(oauthConfig.authUrl + '/oauth2/revoke', formData, false);
}

async function fetchClientOAuth(url: string, formData: FormData, body: boolean = true) {
  const res = await fetch(url, {
    method: 'POST',
    headers: {
      ...getClientHeaders(),
    },
    body: formData,
  });

  if (res.ok) {
    if (body) {
      return res.json();
    }
    return;
  }

  throw res;
}
