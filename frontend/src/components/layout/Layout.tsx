import { Link, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'

export default function Layout({ children }: { children: React.ReactNode }) {
  const { user, isAuthenticated, logout } = useAuthStore()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-blue-700 text-white shadow">
        <div className="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
          <Link to="/" className="text-xl font-bold tracking-tight">⚾ KBO 티켓팅</Link>
          <div className="flex items-center gap-4 text-sm">
            {isAuthenticated ? (
              <>
                <span className="text-blue-200">{user?.name}님</span>
                <Link to="/mypage/reservations" className="hover:text-blue-200">내 예매</Link>
                <button onClick={handleLogout} className="hover:text-blue-200">로그아웃</button>
              </>
            ) : (
              <>
                <Link to="/login" className="hover:text-blue-200">로그인</Link>
                <Link to="/register" className="bg-white text-blue-700 px-3 py-1 rounded font-medium hover:bg-blue-50">
                  회원가입
                </Link>
              </>
            )}
          </div>
        </div>
      </nav>
      <main className="max-w-6xl mx-auto px-4 py-8">{children}</main>
    </div>
  )
}
