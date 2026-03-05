package com.produto.api.mapper;

import com.produto.api.dto.AddProductDTO;
import com.produto.api.dto.ResponseProductDTO;
import com.produto.api.dto.UpdateProductDTO;
import com.produto.api.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntityAdd(AddProductDTO product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product toEntityUpdate(UpdateProductDTO product, @MappingTarget Product entity);

    ResponseProductDTO toDTO(Product product);
}
