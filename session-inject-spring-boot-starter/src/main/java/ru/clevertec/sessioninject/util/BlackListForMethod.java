package ru.clevertec.sessioninject.util;

import java.lang.reflect.Method;
import java.util.List;

public record BlackListForMethod(Method method, List<Class> classes) {
}
