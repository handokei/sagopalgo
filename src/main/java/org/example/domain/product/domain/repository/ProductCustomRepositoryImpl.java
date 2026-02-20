package org.example.domain.product.domain.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.domain.like.domain.model.QProductLike;
import org.example.domain.order.domain.model.QOrderItem;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.QProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> search(Pageable pageable, String sort, Long categoryId, String keyword) {
        QProduct product = QProduct.product;
        QProductLike productLike = QProductLike.productLike;
        QOrderItem orderItem = QOrderItem.orderItem;

        List<Product> result = queryFactory
                .selectFrom(product)
                .leftJoin(product.seller).fetchJoin()
                .leftJoin(product.category).fetchJoin()
                .leftJoin(productLike).on(productLike.productId.eq(product.id))
                .leftJoin(orderItem).on(orderItem.productId.eq(product.id))
                .where(product.isDeleted.eq(false),
                        categoryIdEquals(categoryId, product),
                        titleContains(keyword, product))
                .groupBy(product.id, product.seller.id)
                .orderBy(
                        getOrderSpecifier(sort,
                                product,
                                productLike,
                                orderItem)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .where(product.isDeleted.eq(false),
                        categoryIdEquals(categoryId, product),
                        titleContains(keyword, product))
                .fetchOne();

        return new PageImpl<>(result, pageable, total);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, QProduct product, QProductLike productLike, QOrderItem orderItem) {
        if (sort == null) {
            return product.id.asc();
        }

        return switch (sort) {
            case "price_desc" -> product.price.desc();
            case "price_asc" -> product.price.asc();
            case "likes_count" -> productLike.count().desc();
            case "popular" -> orderItem.count().desc();
            case "latest" -> product.createdAt.desc();
            default -> product.id.asc();
        };
    }

    private BooleanExpression titleContains(String keyword, QProduct product) {
        return keyword == null || keyword.isBlank()
                ? null
                : product.title.contains(keyword);
    }

    private BooleanExpression categoryIdEquals(Long categoryId, QProduct product) {
        return categoryId == null
                ? null
                : product.categoryId.eq(categoryId);
    }
}
