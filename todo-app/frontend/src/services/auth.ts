// Simple authentication service for the Todo app

// Define user roles/groups
const userGroups: Record<string, string[]> = {
  'Alice': ['admin', 'user'],
  'Bob': ['user'],
  'Charlie': ['guest']
};

// Simple user info interface
export interface UserInfo {
  username: string;
  groups: string[];
}

// For this demo, we'll create a simple token with user info
export const generateToken = (username: string): string => {
  // Create a simple token with user info
  const userInfo: UserInfo = {
    username: username,
    groups: userGroups[username] || ['guest']
  };

  // Just base64 encode the user info - this is NOT secure
  // In a real app, you would use a proper JWT with signing
  return btoa(JSON.stringify(userInfo));
};

export const getAuthToken = (): string | null => {
  return localStorage.getItem('authToken');
};

export const setAuthToken = (token: string): void => {
  localStorage.setItem('authToken', token);
};

export const removeAuthToken = (): void => {
  localStorage.removeItem('authToken');
};

// Parse the token to get user info
export const parseToken = (token: string): UserInfo | null => {
  try {
    // Decode the base64 token
    const userInfo = JSON.parse(atob(token));
    return userInfo as UserInfo;
  } catch (error) {
    console.error('Error parsing token:', error);
    return null;
  }
};

export const getCurrentUser = (): string | null => {
  const token = getAuthToken();
  if (!token) return null;

  const userInfo = parseToken(token);
  return userInfo?.username || null;
};

export const getUserGroups = (): string[] => {
  const token = getAuthToken();
  if (!token) return [];

  const userInfo = parseToken(token);
  return userInfo?.groups || [];
};

export const isAdmin = (): boolean => {
  const groups = getUserGroups();
  return groups.includes('admin');
};
