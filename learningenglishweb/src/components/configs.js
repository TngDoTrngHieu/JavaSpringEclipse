import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = "https://javaspringeclipse-production.up.railway.app";

export const endpoints = {
    login: "/api/auth/login",
    register: "/api/auth/register",
    forgotPassword: "/api/auth/forgot-password",
    resetPassword: "/api/auth/reset-password",
    profile: "/api/users/profile",
    updateProfile: "/api/users/update/profile",
    loginGoogle: "/api/auth/google",
    googleClientId: "/api/auth/google-client-id",
    users: "/api/users",
    payments: "/api/payments",
    paymentProcess: "/api/payments/process",
    vocabularies: "/api/vocabularies",
    vipPackages: "/api/vip-packages",
    vocabularyById: (id) => `/api/vocabularies/${id}`,
    vocabularySearch: "/api/vocabularies/search",
    lessons: "/api/lessons",
    lessonById: (id) => `/api/lessons/${id}`,
    lessonTypes: "/api/lesson-types",
    categories: "/api/categories",
    categoryById: (id) => `/api/categories/${id}`,
    categoryTypes: "/api/category-types",
    sections: "/api/sections",
    sectionById: (id) => `/api/sections/${id}`,
    sectionsByLesson: (lessonId) => `/api/sections/lesson/${lessonId}`,
    sectionUploadAudio: "/api/sections/upload-audio",
    sectionTypes: "/api/section-types",
    writingSubmit: "/api/user-writing-answers/submit",
    speakingUpload: "/api/speaking/upload",
    speakingSubmit: "/api/speaking/submit",
    listeningSubmit: "/api/listening/submit",
    studyPlanMy: "/api/study-plans/my",
    studyPlanCreate: "/api/study-plans",
    practiceHistory: "/api/practice-sessions/my",
    changePassword: "/api/users/change-password",

};


const stripContentTypeForFormData = (config) => {
    if (config.data instanceof FormData) {
        const h = config.headers;
        if (h && typeof h.delete === "function") {
            h.delete("Content-Type");
            h.delete("content-type");
        } else if (h) {
            delete h["Content-Type"];
            delete h["content-type"];
        }
    }
    return config;
};

const Apis = axios.create({
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});
Apis.interceptors.request.use(stripContentTypeForFormData);

export const authApis = () => {
    const token = cookie.load("token");
    const instance = axios.create({
        baseURL: BASE_URL,
        headers: {
            Authorization: token ? `Bearer ${token}` : "",
            "Content-Type": "application/json",
        },
    });
    instance.interceptors.request.use(stripContentTypeForFormData);
    return instance;
};

export default Apis;
