import jwt from 'jsonwebtoken';
import { User } from '../types/user';

// Mock secret key (in a real app, this would be on the server)
const JWT_SECRET = 'your-secret-key';

// Define user roles/groups
const userGroups: Record<string, string[]> = {
  'Alice': ['admin', 'user'],
  'Bob': ['user'],
  'Charlie': ['guest']
};

export interface JwtPayload {
  sub: string;
  name: string;
  groups: string[];
  iat: number;
  exp: number;
}

export const generateToken = (username: string): string => {
  // Create a JWT with user info and groups
  return jwt.sign(
    { 
      sub: username,
      name: username,
      groups: userGroups[username] || ['guest'],
    },
    JWT_SECRET,
    { expiresIn: '1h' }
  );
};

export const decodeToken = (token: string): JwtPayload | null => {
  try {
    return jwt.verify(token, JWT_SECRET) as JwtPayload;
  } catch (error) {
    console.error('Invalid token:', error);
    return null;
  }
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

export const getCurrentUser = (): string | null => {
  const token = getAuthToken();
  if (!token) return null;
  
  const decoded = decodeToken(token);
  return decoded?.sub || null;
};

export const getUserGroups = (): string[] => {
  const token = getAuthToken();
  if (!token) return [];
  
  const decoded = decodeToken(token);
  return decoded?.groups || [];
};

export const isAdmin = (): boolean => {
  const groups = getUserGroups();
  return groups.includes('admin');
};
