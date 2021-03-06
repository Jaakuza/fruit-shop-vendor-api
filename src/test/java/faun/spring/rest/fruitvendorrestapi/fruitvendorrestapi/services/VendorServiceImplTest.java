package faun.spring.rest.fruitvendorrestapi.fruitvendorrestapi.services;

import faun.spring.rest.fruitvendorrestapi.fruitvendorrestapi.api.v1.vendors.dto.VendorDTO;
import faun.spring.rest.fruitvendorrestapi.fruitvendorrestapi.api.v1.vendors.mapper.VendorMapper;
import faun.spring.rest.fruitvendorrestapi.fruitvendorrestapi.domain.Vendor;
import faun.spring.rest.fruitvendorrestapi.fruitvendorrestapi.exceptions.ResourceNotFoundException;
import faun.spring.rest.fruitvendorrestapi.fruitvendorrestapi.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

public class VendorServiceImplTest {

    private static final Long INVALID_ID = 999L;

    @Mock
    private VendorRepository vendorRepository;

    private VendorService vendorService;
    private Vendor vendorOne, vendorTwo;
    private VendorDTO vendorDTO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        vendorService = new VendorServiceImpl(vendorRepository, new VendorMapper());
        vendorOne = new Vendor(1L, "First Vendor");
        vendorTwo = new Vendor(2L, "Second Vendor");
        vendorDTO = new VendorDTO(1L, "First DTO Vendor", "");
    }

    @Test
    public void getAllVendors() {
        given(vendorRepository.findAll())
                .willReturn(Arrays.asList(vendorOne, vendorTwo));

        List<VendorDTO> vendorDTOs = vendorService.getAllVendors();

        then(vendorRepository).should().findAll();

        assertNotNull(vendorDTOs);
        assertEquals(2, vendorDTOs.size());
        assertEquals(vendorOne.getId(), vendorDTOs.get(0).getId());
        assertEquals(vendorTwo.getId(), vendorDTOs.get(1).getId());
    }

    @Test
    public void getVendorById() {
        given(vendorRepository.findById(anyLong()))
                .willReturn(Optional.of(vendorOne));

        VendorDTO foundVendor = vendorService.getVendorById(1L);

        then(vendorRepository).should().findById(anyLong());

        assertNotNull(foundVendor);
        assertEquals(vendorOne.getId(), foundVendor.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getVendorByIdThrowsResourceNotFoundException() {
        given(vendorRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        vendorService.getVendorById(INVALID_ID);
    }

    @Test
    public void testAddVendor() {
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(vendorOne);

        VendorDTO addedVendor = vendorService.addVendor(vendorDTO);

        then(vendorRepository).should().save(any(Vendor.class));

        assertNotNull(addedVendor);
        assertEquals(vendorOne.getId(), addedVendor.getId());
    }

    @Test
    public void testDeleteVendor() {
        vendorService.deleteVendor(vendorDTO.getId());

        then(vendorRepository).should().deleteById(vendorDTO.getId());
    }

    @Test
    public void testUpdateVendor() {
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(vendorOne);

        VendorDTO updatedVendorDTO = vendorService.updateVendorById(vendorOne.getId(), vendorDTO);

        assertNotNull(updatedVendorDTO);
        assertEquals(vendorOne.getId(), updatedVendorDTO.getId());
        assertEquals(vendorOne.getName(), updatedVendorDTO.getName());
        assertTrue(updatedVendorDTO.getVendorUrl().contains("/" + vendorOne.getId()));
    }

    @Test
    public void testUpdateFieldsInVendorById() {
        given(vendorRepository.findById(vendorOne.getId()))
                .willReturn(Optional.of(vendorOne));

        Vendor resultVendor = new Vendor(vendorOne.getId(), vendorDTO.getName());

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(resultVendor);

        // Basically does the same as testUpdateVendor() because there is only one field in the Vendor class
        VendorDTO updatedVendorDTO = vendorService.updateFieldsInVendorById(vendorOne.getId(), vendorDTO);

        then(vendorRepository).should().findById(anyLong());
        then(vendorRepository).should().save(any(Vendor.class));

        assertNotNull(updatedVendorDTO);
        assertEquals(vendorOne.getId(), updatedVendorDTO.getId());
        assertEquals(vendorOne.getName(), updatedVendorDTO.getName());
        assertTrue(updatedVendorDTO.getVendorUrl().contains("/" + vendorOne.getId()));
    }
}