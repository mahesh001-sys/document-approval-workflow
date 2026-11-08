import axiosClient from "./axiosClient";

const authService = {
  async register(firstName, lastName, email, password) {
    const response = await axiosClient.post("/users", {
      firstName,
      lastName,
      email,
      password
    });

    return response.data;
  },

  async login(email, password) {
    const response = await axiosClient.post("/auth/login", {
      email,
      password
    });

    const data = response.data;
    const token = data.accessToken || data.token;

    if (token) {
      localStorage.setItem("accessToken", token);
    }

    // The backend AuthResponse returns { accessToken, role }.
    // Never trust a role supplied by the registration form.
    const user = {
      ...(data.user || {}),
      role: data.user?.role || data.role || null
    };

    localStorage.setItem("user", JSON.stringify(user));

    return data;
  },

  logout() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("user");
  },

  getToken() {
    return localStorage.getItem("accessToken");
  },

  getUser() {
    const user = localStorage.getItem("user");

    if (!user) return null;

    try {
      return JSON.parse(user);
    } catch {
      return null;
    }
  },

  isAuthenticated() {
    return Boolean(localStorage.getItem("accessToken"));
  }
};

export default authService;
