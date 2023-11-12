package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.repository.CompanyAdminRepository;
import rs.ac.uns.ftn.informatika.jpa.repository.SystemAdminRepository;

@Service
public class SystemAdminService {

    @Autowired
    private SystemAdminRepository systemAdminRepository;
}
