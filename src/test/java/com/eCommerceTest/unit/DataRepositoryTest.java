package com.eCommerceTest.unit;

import com.eCommerceTest.utils.DataRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class DataRepositoryTest {

    @Test
    public void testCachingSameInstanceReturned() {
        List<Map<String,Object>> first = DataRepository.load("/testData/users.json", new TypeReference<List<Map<String,Object>>>(){});
        List<Map<String,Object>> second = DataRepository.load("/testData/users.json", new TypeReference<List<Map<String,Object>>>(){});
        Assert.assertSame(first, second, "Expected cached instance to be reused");
        Assert.assertFalse(first.isEmpty(), "Users test data should not be empty");
    }

    @Test
    public void testEvictReloadCreatesNewInstance() {
        List<Map<String,Object>> beforeEvict = DataRepository.load("/testData/users.json", new TypeReference<List<Map<String,Object>>>(){});
        DataRepository.evict("/testData/users.json");
        List<Map<String,Object>> afterEvict = DataRepository.load("/testData/users.json", new TypeReference<List<Map<String,Object>>>(){});
        Assert.assertNotSame(beforeEvict, afterEvict, "Evict should force new instance load");
        Assert.assertEquals(beforeEvict.size(), afterEvict.size(), "Size should remain same after reload");
    }

    @Test
    public void testClearReloadCreatesNewInstance() {
        List<Map<String,Object>> first = DataRepository.load("/testData/users.json", new TypeReference<List<Map<String,Object>>>(){});
        DataRepository.clear();
        List<Map<String,Object>> second = DataRepository.load("/testData/users.json", new TypeReference<List<Map<String,Object>>>(){});
        Assert.assertNotSame(first, second, "Clear should force new instance load");
        Assert.assertEquals(first.size(), second.size(), "Size should remain same after clear reload");
    }
}
