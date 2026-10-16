package com.healthflow.infrastructure.persistence.mapper;


import com.healthflow.domain.model.user.NationalId;
import com.healthflow.domain.model.user.User;
import com.healthflow.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(source = "userRoleList", target = "roles")
    UserEntity toEntity(User user);

    @Mapping(source = "roles", target = "userRoleList")
    User toDomain(UserEntity userEntity);

    default String toString(NationalId nationalId) {
        return nationalId == null ? null : nationalId.getValue();
    }

    default NationalId toNationalId(String value) {
        return value == null ? null : new NationalId(value);
    } // yapay zeka önerdi bunu neden yapıyoruz öğrenmeliyim.
    /*
    * MapStruct, kaynak ve hedef tiplerine bakarak bu default metotları kullanır:
        - Domain’den entity’ye geçerken NationalId içindeki metni getValue() ile çıkarır.
        - Entity’den domain’e dönerken new NationalId(value) çağırır. Böylece senin uzunluk, rakam ve son basamak kontrollerin çalışır.
    null kontrolleri dönüşüm metotlarının boş değeri aktarabilmesini sağlar; domain’de null kabul edileceği anlamına gelmez.
    * User constructor’ındaki kontrol bunu yine reddeder.
    * Bunu ekleyip projeyi derle. Derleme başarılıysa sonraki adımda PatientPersistenceMapper,
    *  içindeki kullanıcıyı dönüştürmek için bu mapper’ı kullanacak.*/
}
