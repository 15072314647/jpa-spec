package com.github.wenhao.jpa.integration;

import com.github.wenhao.jpa.Specifications;
import com.github.wenhao.jpa.builder.PersonBuilder;
import com.github.wenhao.jpa.model.Person;
import com.github.wenhao.jpa.model.Phone;
import com.github.wenhao.jpa.repository.PersonRepository;
import com.github.wenhao.jpa.repository.PhoneRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Range.Bound.inclusive;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JoinTest {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PhoneRepository phoneRepository;

    @Test
    public void should_be_able_to_find_by_using_many_to_one_query() {
        // given
        Person jack = new PersonBuilder()
            .name("Jack")
            .age(18)
            .phone("iPhone", "139000000000")
            .phone("HuaWei", "13600000000")
            .phone("HuaWei", "18000000000")
            .phone("Samsung", "13600000000")
            .build();

        Set<Phone> jackPhones = jack.getPhones();
        for (Phone phone : jackPhones) {
            phone.setPerson(jack);
        }
        personRepository.save(jack);

        // when
        Specification<Phone> specification = Specifications.<Phone>and()
            .eq("brand", "HuaWei")
            .eq(StringUtils.isNotBlank(jack.getName()), "person.name", jack.getName())
            .build();

        List<Phone> phones = phoneRepository.findAll(specification);

        // then
        assertThat(phones.size()).isEqualTo(2);
    }

    @Test
    public void should_be_able_to_find_by_using_many_to_many_query() {
        // given
        Person jack = new PersonBuilder()
            .name("Jack")
            .age(18)
            .address("Sichuan", 3)
            .address("Sichuan", 5)
            .address("Chengdu", 4)
            .address("Zhonghe", 7)
            .build();

        Person eric = new PersonBuilder()
            .name("Eric")
            .age(20)
            .address("GaoXin", 8)
            .address("Tianfu", 9)
            .address("Chengdu", 4)
            .build();

        Person alex = new PersonBuilder()
            .name("Alex")
            .age(30)
            .address("HuaYang", 1)
            .address("NeiJiang", 2)
            .build();

        personRepository.save(jack);
        personRepository.save(eric);
        personRepository.save(alex);

        // when
        Specification<Person> specification = Specifications.<Person>and()
            .between("age", Range.of(inclusive(10), inclusive(35)))
            .eq("addresses.street", "Chengdu")
            .build();

        List<Person> phones = personRepository.findAll(specification);

        // then
        assertThat(phones.size()).isEqualTo(2);
    }

}
