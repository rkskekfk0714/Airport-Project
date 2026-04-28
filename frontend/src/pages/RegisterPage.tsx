import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi } from '../api/auth'
import { useAuthStore } from '../store/authStore'

export default function RegisterPage() {
  const navigate = useNavigate()
  const { setUser } = useAuthStore()
  const [form, setForm] = useState({ email: '', password: '', name: '', phone: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await authApi.register(form)
      setUser(res.user, res.accessToken)
      navigate('/')
    } catch (err: any) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-sm mx-auto mt-10">
      <h1 className="text-2xl font-bold mb-6 text-center">회원가입</h1>
      <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-sm border p-6 space-y-4">
        {[
          { label: '이메일', key: 'email', type: 'email', placeholder: 'email@example.com' },
          { label: '비밀번호', key: 'password', type: 'password', placeholder: '8자리 이상' },
          { label: '이름', key: 'name', type: 'text', placeholder: '홍길동' },
          { label: '전화번호', key: 'phone', type: 'tel', placeholder: '010-1234-5678' },
        ].map(({ label, key, type, placeholder }) => (
          <div key={key}>
            <label className="text-sm text-gray-600 block mb-1">{label}</label>
            <input
              type={type}
              value={form[key as keyof typeof form]}
              onChange={(e) => setForm({ ...form, [key]: e.target.value })}
              className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"
              placeholder={placeholder}
              required
            />
          </div>
        ))}
        {error && <div className="text-red-500 text-sm">{error}</div>}
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? '처리 중...' : '회원가입'}
        </button>
      </form>
      <p className="text-center text-sm text-gray-500 mt-4">
        이미 계정이 있으신가요?{' '}
        <Link to="/login" className="text-blue-600 hover:underline">로그인</Link>
      </p>
    </div>
  )
}
