package site.easy.to.build.crm.service.customer;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.csv.CsvValidationException;
import site.easy.to.build.crm.csv.GenericCsvService;
import site.easy.to.build.crm.csv.dto.CsvErrorWrapper;
import site.easy.to.build.crm.csv.dto.CustomerCsvDto;
import site.easy.to.build.crm.entity.CustomerLoginInfo;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.util.EmailTokenUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final GenericCsvService<CustomerCsvDto, Customer> genericCsvService;
    private final CustomerRepository customerRepository;
    private CustomerLoginInfoServiceImpl customerLoginInfoService;

    @Override
    public Customer findByCustomerId(int customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public List<Customer> findByUserId(int userId) {
        return customerRepository.findByUserId(userId);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }

    @Override
    public List<Customer> getRecentCustomers(int userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return customerRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public long countByUserId(int userId) {
        return customerRepository.countByUserId(userId);
    }

    // csv
    @Transactional
    public List<Customer> importCsv(MultipartFile file, User user) throws IOException, CsvValidationException {
        List<Customer> entities = new ArrayList<>();
        List<CustomerCsvDto> dtos = new ArrayList<>();
        List<CsvErrorWrapper> errors = new ArrayList<>();

        String fileName = file.getOriginalFilename();
        try {
            dtos = genericCsvService.getDtosFromCsv(file, CustomerCsvDto.class, fileName);
        } catch (CsvValidationException e) {
            errors.addAll(e.getErrors());
        }

        for (int i = 0; i < dtos.size(); i++) {
            CustomerCsvDto dto = dtos.get(i);
            entities.add(convertToEntity(dto, user, errors, i + 1, fileName));
        }

        if (!errors.isEmpty()) {
            throw new CsvValidationException("dto->customer", errors);
        }

        return entities;
    }

    @Transactional
    public Customer convertToEntity(
            CustomerCsvDto csvDto,
            User user,
            List<CsvErrorWrapper> errors,
            int rowIndex,
            String fileName
    ) {
        Customer existing = customerRepository.findByEmail(csvDto.getCustomer_email());
        if (existing != null) {
            String msg = "Duplicate email '" + existing.getEmail() + "'!";
            errors.add(new CsvErrorWrapper(fileName, rowIndex, msg, csvDto.toString()));
        }

        Customer customer = new Customer();
        customer.setEmail(csvDto.getCustomer_email());
        customer.setName(csvDto.getCustomer_name());
        customer.setUser(user);
        customer.setCountry("Madagascar");

        CustomerLoginInfo customerLoginInfo = new CustomerLoginInfo();
        customerLoginInfo.setEmail(csvDto.getCustomer_email());
        customerLoginInfo.setToken(EmailTokenUtils.generateToken());
        customerLoginInfo.setPasswordSet(false);

        customerLoginInfo.setCustomer(customer);
        customerLoginInfoService.save(customerLoginInfo);

        customer.setCustomerLoginInfo(customerLoginInfo);
        return customer;
    }
}
