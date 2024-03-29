/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/**
 * 在所有 {@code @Configuration} bean 之后运行的 {@link ImportSelector} 变体
 * 已经处理。这种类型的选择器在选定时特别有用
 * 进口是 {@code @Conditional}。
 *
 * <p>实现也可以扩展 {@link org.springframework.core.Ordered}
 * 接口或使用 {@link org.springframework.core.annotation.Order} 注释来
 * 表示优先于其他 {@link DeferredImportSelector DeferredImportSelectors}。
 *
 * <p>实现还可以提供一个 {@link getImportGroup() 导入组}
 * 可以在不同的选择器之间提供额外的排序和过滤逻辑。
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @since 4.0
 */
public interface DeferredImportSelector extends ImportSelector {

    /**
     * R返回特定的导入组。 <p>默认实现返回 {@code null} 不需要分组。
     *
     * @return the import group class, or {@code null} if none
     * @since 5.0
     */
    @Nullable
    default Class<? extends Group> getImportGroup() {
        return null;
    }


    /**
     * 用于对来自不同导入选择器的结果进行分组的接口。
     *
     * @since 5.0
     */
    interface Group {

        /**
         * Process the {@link AnnotationMetadata} of the importing @{@link Configuration}
         * class using the specified {@link DeferredImportSelector}.
         */
        void process(AnnotationMetadata metadata, DeferredImportSelector selector);

        /**
         * 返回应导入哪个类的 {@link Entry 条目}对于这个组。
         */
        Iterable<Entry> selectImports();


        /**
         * 包含导入的 {@link AnnotationMetadata} 的条目
         * {@link Configuration} class and the class name to import.
         */
        class Entry {

            private final AnnotationMetadata metadata;

            private final String importClassName;

            public Entry(AnnotationMetadata metadata, String importClassName) {
                this.metadata = metadata;
                this.importClassName = importClassName;
            }

            /**
             * Return the {@link AnnotationMetadata} of the importing
             * {@link Configuration} class.
             */
            public AnnotationMetadata getMetadata() {
                return this.metadata;
            }

            /**
             * Return the fully qualified name of the class to import.
             */
            public String getImportClassName() {
                return this.importClassName;
            }

            @Override
            public boolean equals(@Nullable Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || getClass() != other.getClass()) {
                    return false;
                }
                Entry entry = (Entry) other;
                return (this.metadata.equals(entry.metadata) && this.importClassName.equals(entry.importClassName));
            }

            @Override
            public int hashCode() {
                return (this.metadata.hashCode() * 31 + this.importClassName.hashCode());
            }

            @Override
            public String toString() {
                return this.importClassName;
            }
        }
    }

}
