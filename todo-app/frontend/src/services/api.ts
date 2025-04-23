import axios from 'axios';
import { Todo, CreateTodoParams } from '../types/todo';

const API_URL = '';  // Empty string to use the proxy configured in package.json

// Create axios instance with base URL
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// API functions for todos
export const todoApi = {
  // Get all todos
  getAllTodos: async (): Promise<Todo[]> => {
    try {
      const response = await api.get('/todos');
      return response.data;
    } catch (error) {
      console.error('Error fetching todos:', error);
      return [];
    }
  },

  // Get a single todo by ID
  getTodoById: async (id: string): Promise<Todo | null> => {
    try {
      const response = await api.get(`/todos/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching todo with ID ${id}:`, error);
      return null;
    }
  },

  // Create a new todo
  createTodo: async (todoData: CreateTodoParams): Promise<Todo | null> => {
    try {
      const response = await api.post('/todos', todoData);
      return response.data;
    } catch (error) {
      console.error('Error creating todo:', error);
      return null;
    }
  },

  // Mark a todo as complete
  markComplete: async (id: string): Promise<Todo | null> => {
    try {
      const response = await api.post(`/todos/${id}/complete`);
      return response.data;
    } catch (error) {
      console.error(`Error marking todo ${id} as complete:`, error);
      return null;
    }
  },

  // Move a todo to trash
  moveToTrash: async (id: string): Promise<Todo | null> => {
    try {
      const response = await api.post(`/todos/${id}/trash`);
      return response.data;
    } catch (error) {
      console.error(`Error moving todo ${id} to trash:`, error);
      return null;
    }
  },
};
