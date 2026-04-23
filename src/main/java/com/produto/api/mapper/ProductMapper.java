package com.produto.api.mapper;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.response.product.ResponseProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    Product toEntityAdd(AddProductDTO product);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(UpdateProductDTO product, @MappingTarget Product entity);

    ResponseProductDTO toDTO(Product product);
}
