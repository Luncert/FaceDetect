package integration;

import org.apache.commons.lang3.RandomStringUtils;
import org.luncert.facedetect.model.Student;
import org.luncert.facedetect.model.Teacher;
import org.luncert.objectmocker.ObjectMocker;
import org.luncert.objectmocker.core.ObjectGenerator;
import org.luncert.objectmocker.core.ObjectMockContext;

import static org.luncert.objectmocker.builtingenerator.BuiltinGeneratorBuilder.*;

public class GlobalObjectMockContext {

    private GlobalObjectMockContext() {
    }

    private static ObjectMockContext CONTEXT;
    /**
     * Provide a basic context with some pre-registered ObjectGenerator.
     *
     * @return ObjectMockContext
     */
    public static ObjectMockContext getInstance() {
        if (CONTEXT == null) {
            synchronized (GlobalObjectMockContext.class) {
                if (CONTEXT == null) {
                    CONTEXT = ObjectMocker.context().create();
                    try {
                        // 初始化CONTEXT，注册需要的对象生成器
                        CONTEXT.register(ObjectGenerator.builder(Student.class)
                                .addIgnores("faceData")
                                .field("id", (ctx, cls) -> "20162202" + RandomStringUtils.randomNumeric(5))
                                .field("name", stringGenerator(6))
                                .build());

                        CONTEXT.register(ObjectGenerator.builder(Teacher.class)
                                .addIgnores("id")
                                .field("name", (ctx, cls) -> "Teacher" + RandomStringUtils.randomAlphabetic(4))
                                .build());
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return CONTEXT;
    }
}
