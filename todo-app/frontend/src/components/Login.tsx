import React, { useState, useEffect } from 'react';
import { User } from '../types/user';
import { userApi } from '../services/api';
import { generateToken, setAuthToken } from '../services/auth';

interface LoginProps {
  onLogin: (username: string) => void;
}

const Login: React.FC<LoginProps> = ({ onLogin }) => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [loginInProgress, setLoginInProgress] = useState<string | null>(null);

  useEffect(() => {
    const fetchUsers = async () => {
      setLoading(true);
      try {
        const data = await userApi.getAllUsers();
        setUsers(data);
        setError(null);
      } catch (err) {
        setError('Failed to fetch users. Please try again later.');
        console.error('Error fetching users:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  const handleLogin = async (username: string) => {
    setLoginInProgress(username);
    setError(null);

    try {
      // Generate JWT token asynchronously
      const token = await generateToken(username);
      setAuthToken(token);
      onLogin(username);
    } catch (error) {
      console.error('Error generating token:', error);
      setError('Failed to login. Please try again.');
    } finally {
      setLoginInProgress(null);
    }
  };

  return (
    <div className="login-container" style={{
      maxWidth: '400px',
      margin: '100px auto',
      padding: '20px',
      borderRadius: '8px',
      boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
      backgroundColor: 'white'
    }}>
      <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Login to Todo App</h2>

      {error && (
        <div style={{
          backgroundColor: '#ffebee',
          color: '#c62828',
          padding: '10px',
          borderRadius: '4px',
          marginBottom: '20px'
        }}>
          {error}
        </div>
      )}

      {loading ? (
        <p style={{ textAlign: 'center' }}>Loading users...</p>
      ) : (
        <div>
          <p style={{ marginBottom: '15px', textAlign: 'center' }}>Select a user to continue:</p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {users.map(user => (
              <button
                key={user.username}
                onClick={() => handleLogin(user.username)}
                disabled={loginInProgress !== null}
                style={{
                  padding: '12px',
                  backgroundColor: loginInProgress === user.username ? '#BBDEFB' : '#2196F3',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: loginInProgress !== null ? 'default' : 'pointer',
                  fontSize: '16px',
                  transition: 'background-color 0.3s',
                  position: 'relative',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}
                onMouseOver={(e) => {
                  if (loginInProgress === null) {
                    e.currentTarget.style.backgroundColor = '#1976D2';
                  }
                }}
                onMouseOut={(e) => {
                  if (loginInProgress === null) {
                    e.currentTarget.style.backgroundColor = '#2196F3';
                  }
                }}
              >
                <span>{loginInProgress === user.username ? 'Logging in...' : user.username}</span>
                <div style={{ display: 'flex', gap: '4px' }}>
                  {user.groups?.map((group: string, index: number) => (
                    <span
                      key={index}
                      style={{
                        backgroundColor: group === 'admins' ? '#ff9800' :
                                        group === 'users' ? '#4caf50' : '#9e9e9e',
                        color: 'white',
                        padding: '2px 6px',
                        borderRadius: '10px',
                        fontSize: '12px',
                        fontWeight: 'bold'
                      }}
                    >
                      {group}
                    </span>
                  ))}
                </div>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Login;
