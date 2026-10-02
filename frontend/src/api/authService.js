import axiosClient from "./axiosClient";

const authService = {
  async login(email, password) {
    const response = await axiosClient.post("/auth/login", {
      email,
      password
    });

    const data = response.data;

    if (data.token) {
      localStorage.setItem("accessToken", data.token);
    }

    if (data.accessToken) {
      localStorage.setItem("accessToken", data.accessToken);
    }

    if (data.user) {
      localStorage.setItem(
        "user",
        JSON.stringify(data.user)
      );
    }

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

    if (!user) {
      return null;
    }

    try {
      return JSON.parse(user);
    } catch {
      return null;
    }
  },

  isAuthenticated() {
    return Boolean(
      localStorage.getItem("accessToken")
    );
  }
};

export default authService;
