export interface UserLogin {
  username: string;
  password: string;
}

export interface User {
  displayName: string;
  username: string;
  password: string;
}

export interface Category {
  id?: number;
  name: string;
}

export interface Product {
  id?: number;
  name: string;
  description: string;
  price: number;
  category: Category;
  imageName?: string;
  contentType?: string;
}
