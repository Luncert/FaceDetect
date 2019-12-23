export default ({
    user: {
        signin:     'user/signIn',
        teacher: {
            getLeaveSlips: 'user/teacher/leaveSlips',
            getCourses: 'user/teacher/courses',
            getStudents: 'user/teacher/students',
            createCourse: '/user/teacher/course',
            postLeaveSlipResult: (leaveSlipID: number) => `/user/teacher/leaveSlip:${leaveSlipID}`,
            course: {
                getSignInList: (courseID: number) => `/user/teacher/course:${courseID}/signInList`,
                removeCourse: (courseID: number) => `/user/teacher/course:${courseID}`
            }
        },
        student: {
        }
    }
})