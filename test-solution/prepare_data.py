from requests import post, Session

def check_status_code(rep, msg):
    if rep.status_code is not 200:
        print(msg + ':', rep.content)
        quit()


# create students
names = ['李经纬', '刘润扬', '赖瑞', '唐朝', '刘昌澍', '樊俊材', '王超', '杨正国', '李宇', '董藩', '鹿城']
student_id_list = []
for i in range(11):
    rep = post('http://localhost:8080/user/student?id=201622020400{}&name={}'.format(i, names[i]))
    check_status_code(rep, 'Create student failed')
    student_id_list.append('201622020400%d' % i)

# create teacher
rep = post('http://localhost:8080/user/teacher?name=张翰林')
check_status_code(rep, 'Create teacher failed')
teacher_id = rep.content

# login as teacher
session = Session()
rep = session.post('http://localhost:8080/user/signIn', data={'account': teacher_id, 'password': '123456'})
check_status_code(rep, 'Login failed')

# create course
# rep = session.post('http://localhost:8080/user/teacher/course', data={'name': '软件安全设计', 'studentIDList': student_id_list})
# check_status_code(rep, 'Create course failed')
