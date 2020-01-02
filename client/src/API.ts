export default ({
    user: {
        signin: '/user/signIn',
        teacher: {
            getLeaveSlips: '/user/teacher/leaveSlips',
            getCourses: '/user/teacher/courses',
            getStudents: '/user/teacher/students',
            createCourse: '/user/teacher/course',
            postLeaveSlipResult: (leaveSlipID: number) => `/user/teacher/leaveSlip:${leaveSlipID}`,
            course: {
                getSignInList: (courseID: number) => `/user/teacher/course:${courseID}/signInList`,
                removeCourse: (courseID: number) => `/user/teacher/course:${courseID}`,
                startSignIn: (courseID: number) => `/user/teacher/course:${courseID}/signIn/start`,
                signInStudents: (courseID: number, signInID: number) => `/user/teacher/course:${courseID}/signIn:${signInID}/students`,
                stopSignIn: (courseID: number, signInID: number) => `/user/teacher/course:${courseID}/signIn:${signInID}/stop`
            }
        },
        student: {
             getProfile: '/user/student/profile',
             getCourseInfo: '/user/student/courses',
             applyLeaveSlip: (courseID: number) => `/user/student/course:${courseID}/leaveSlip`
        }
    }
})