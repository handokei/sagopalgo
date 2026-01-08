package org.example.domain.product.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.domain.like.domain.model.QProductLike;
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
    public Page<Product> search(int likeCount, Pageable pageable) {
        QProduct product = QProduct.product;
        QProductLike productLike = QProductLike.productLike;
        List<Product> result = queryFactory.selectFrom(product)
                .leftJoin(productLike).on(productLike.id.eq(product.id))
                .groupBy(product.id)
                .having(productLike.count().goe(likeCount))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(result);
    }
}
