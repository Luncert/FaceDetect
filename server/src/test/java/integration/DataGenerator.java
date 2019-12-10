package integration;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.luncert.facedetect.model.*;
import org.luncert.facedetect.repo.CourseRepo;
import org.luncert.facedetect.repo.StudentRepo;
import org.luncert.facedetect.repo.TeacherRepo;
import org.luncert.facedetect.repo.UserInfoRepo;
import org.luncert.objectmocker.core.ObjectMockContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

class DataGenerator {

    private ObjectMockContext mockContext = GlobalObjectMockContext.getInstance();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private TeacherRepo teacherRepo;

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private CourseRepo courseRepo;

    Triple<Student, UserInfo, String> genStudent() {
        Student student = mockContext.generate(Student.class);
        student = studentRepo.save(student);
        Pair<UserInfo, String> pair = genUserInfo(String.valueOf(student.getId()));
        return ImmutableTriple.of(student, pair.getLeft(), pair.getRight());
    }

    Triple<Teacher, UserInfo, String> genTeacher() {
        Teacher teacher = mockContext.generate(Teacher.class);
        teacher = teacherRepo.save(teacher);
        Pair<UserInfo, String> pair = genUserInfo(String.valueOf(teacher.getId()));
        return ImmutableTriple.of(teacher, pair.getLeft(), pair.getRight());
    }

    Pair<UserInfo, String> genUserInfo(String objectID) {
        UserInfo userInfo = new UserInfo();
        userInfo.setAccount(new UserAccount(objectID, UserRole.Teacher));
        String password = RandomStringUtils.randomAlphabetic(6);
        userInfo.setPassword(passwordEncoder.encode(password));
        return ImmutablePair.of(userInfoRepo.save(userInfo), password);
    }

    void cleanDatabase() {
        // 先清空CourseRepo再清空StudentRepo：先删除外键约束，才能删除外键父表
        courseRepo.deleteAll();
        studentRepo.deleteAll();
        teacherRepo.deleteAll();
        userInfoRepo.deleteAll();
    }
}
