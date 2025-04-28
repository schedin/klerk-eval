export interface Todo {
  todoID: string;
  title: string;
  description: string;
  state?: 'Created' | 'Completed' | 'Trashed';
  createdAt?: string; // ISO string format from backend
  username?: string; // Username of the creator
}

export interface CreateTodoParams {
  title: string;
  description: string;
}
