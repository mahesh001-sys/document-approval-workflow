
import axiosClient from "./axiosClient";

const requestService = {
  async getActiveRequestTypes() {
    const response = await axiosClient.get("/request-types/active");
    return response.data;
  },

  async createRequest(request) {
    const response = await axiosClient.post("/requests", request);
    return response.data;
  },

  async getAllRequests() {
    const response = await axiosClient.get("/requests");
    return response.data;
  },

  async getMyRequests() {
    const response = await axiosClient.get("/requests/my");
    return response.data;
  },

  async getRequestById(id) {
    const response = await axiosClient.get(`/requests/${id}`);
    return response.data;
  },

  async getRequestByNumber(requestNumber) {
    const response = await axiosClient.get(
      `/requests/number/${encodeURIComponent(requestNumber)}`
    );
    return response.data;
  },

  async getRequestsByStatus(status) {
    const response = await axiosClient.get(`/requests/status/${status}`);
    return response.data;
  },

  async getApprovalHistory(requestId) {
    const response = await axiosClient.get(
      `/approval-history/request/${requestId}`
    );
    return response.data;
  },

  async submitRequest(id) {
    const response = await axiosClient.post(`/requests/${id}/submit`);
    return response.data;
  },

  async updateRequest(id, request) {
    const response = await axiosClient.put(`/requests/${id}`, request);
    return response.data;
  },

  async deleteRequest(id) {
    await axiosClient.delete(`/requests/${id}`);
  },

  // Manager workflow
  async startManagerReview(requestId) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/manager/review`
    );
    return response.data;
  },

  async approveManagerRequest(requestId, comments = "") {
    const response = await axiosClient.post(
      `/workflow/${requestId}/manager/approve`,
      null,
      { params: comments.trim() ? { comments: comments.trim() } : {} }
    );
    return response.data;
  },

  async rejectManagerRequest(requestId, comments) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/manager/reject`,
      null,
      { params: { comments: comments.trim() } }
    );
    return response.data;
  },

  async returnManagerRequest(requestId, comments) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/manager/return`,
      null,
      { params: { comments: comments.trim() } }
    );
    return response.data;
  },

  // Admin workflow
  async startAdminReview(requestId) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/admin/review`
    );
    return response.data;
  },

  async approveAdminRequest(requestId, comments = "") {
    const response = await axiosClient.post(
      `/workflow/${requestId}/admin/approve`,
      null,
      { params: comments.trim() ? { comments: comments.trim() } : {} }
    );
    return response.data;
  },

  async rejectAdminRequest(requestId, comments) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/admin/reject`,
      null,
      { params: { comments: comments.trim() } }
    );
    return response.data;
  },

  async returnAdminRequest(requestId, comments) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/admin/return`,
      null,
      { params: { comments: comments.trim() } }
    );
    return response.data;
  }
};

export default requestService;
