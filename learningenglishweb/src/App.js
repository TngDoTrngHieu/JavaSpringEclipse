import Header from "./components/layout/Head";
import Footer from "./components/layout/Footer";
import Home from "./components/Home";
import Login from "./components/Login";
import Register from "./components/Register";
import Profile from "./components/Profile";
import Dashboard from "./components/Dashboard";
import ForgotPage from "./components/ForgotPage";
import ResetPasswordPage from "./components/ResetPasswordPage";
import Vocabulary from "./components/Vocabulary";
import UpgradeVip from "./components/UpgradeVip";
import ThankYouVip from "./components/ThankYouVip";
import ChatBox from "./components/ChatBox";
import AdminLayout from "./components/admin/AdminLayout";
import AdminDashboard from "./components/admin/AdminDashboard";
import AdminCategories from "./components/admin/AdminCategories";
import AdminLessons from "./components/admin/AdminLessons";
import AdminLessonSections from "./components/admin/AdminLessonSections";
import AdminAIGenerateQuiz from "./components/AIGenerateQuiz";
import WritingPage from "./components/WritingPage";
import SpeakingPage from "./components/SpeakingPage";
import ListeningPage from "./components/ListeningPage";
import ReadingPage from "./components/ReadingPage";
import ProgressPage from "./components/ProgressPage";
import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";
import { Container } from "react-bootstrap";


import "bootstrap/dist/css/bootstrap.min.css";

const AppShell = () => {
  const location = useLocation();
  const isAdmin = location.pathname.startsWith("/admin");

  return (
    <>
      {!isAdmin && <Header />}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/forgot" element={<ForgotPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />
        <Route path="/register" element={<Register />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/writing/:lessonId" element={<WritingPage />} />
        <Route path="/speaking/:lessonId" element={<SpeakingPage />} />
        <Route path="/listening/:lessonId" element={<ListeningPage />} />
        <Route path="/reading/:lessonId" element={<ReadingPage />} />
        <Route path="/vocabulary" element={<Vocabulary />} />
        <Route path="/chat" element={<ChatBox />} />
        <Route path="/upgrade-vip" element={<UpgradeVip />} />
        <Route path="/thankyou" element={<ThankYouVip />} />
        <Route path="/progress" element={<ProgressPage />} />
        <Route path="/ai-generate-quiz" element={<AdminAIGenerateQuiz />} />
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<AdminDashboard />} />
          <Route path="categories" element={<AdminCategories />} />
          <Route path="lessons" element={<AdminLessons />} />
          <Route path="lessons/:lessonId/sections" element={<AdminLessonSections />} />
        </Route>
      </Routes>
      {!isAdmin && <Footer />}
    </>
  );
};

const App = () => {
  return (
    <Container fluid className="px-0">
      <BrowserRouter>
        <AppShell />
      </BrowserRouter>
    </Container>
  );
};

export default App;
