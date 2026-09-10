package com.fixit.config;

import com.fixit.entity.Category;
import com.fixit.entity.Location;
import com.fixit.entity.User;
import com.fixit.enums.Role;
import com.fixit.repository.CategoryRepository;
import com.fixit.repository.LocationRepository;
import com.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Master accounts already exist
        }

        System.out.println("🌱 Seeding essential master data (Accounts, Categories, Locations)...");

        // 1. Master Users (Admins, Technicians, Sample Student Accounts)
        User admin = new User("Campus Administrator", "admin", "admin@campus.edu", passwordEncoder.encode("Admin@123"), Role.ROLE_ADMIN, "+1-555-0100", "Administration");
        userRepository.save(admin);

        User techRavi = new User("Ravi Kumar", "ravi_tech", "ravi.tech@campus.edu", passwordEncoder.encode("Tech@123"), Role.ROLE_TECHNICIAN, "+1-555-0121", "Electrical");
        User techArun = new User("Arun Sharma", "arun_tech", "arun.tech@campus.edu", passwordEncoder.encode("Tech@123"), Role.ROLE_TECHNICIAN, "+1-555-0122", "IT & Equipment");
        User techRajesh = new User("Rajesh Verma", "rajesh_tech", "rajesh.tech@campus.edu", passwordEncoder.encode("Tech@123"), Role.ROLE_TECHNICIAN, "+1-555-0123", "Plumbing & Sanitization");
        userRepository.save(techRavi);
        userRepository.save(techArun);
        userRepository.save(techRajesh);

        User studentRahul = new User("Rahul Verma", "rahul123", "rahul.student@campus.edu", passwordEncoder.encode("Student@123"), Role.ROLE_STUDENT, "+1-555-0150", null);
        User studentPriya = new User("Priya Patel", "priya123", "priya.student@campus.edu", passwordEncoder.encode("Student@123"), Role.ROLE_STUDENT, "+1-555-0151", null);
        User studentAnanya = new User("Ananya Sharma", "ananya456", "ananya.student@campus.edu", passwordEncoder.encode("Student@123"), Role.ROLE_STUDENT, "+1-555-0152", null);
        userRepository.save(studentRahul);
        userRepository.save(studentPriya);
        userRepository.save(studentAnanya);

        // 2. Campus Categories
        categoryRepository.save(new Category("Electrical", "Lights, fans, sockets, wiring, and power outages", "zap"));
        categoryRepository.save(new Category("IT / Wi-Fi", "Internet connectivity, routers, projectors, computers", "wifi"));
        categoryRepository.save(new Category("Plumbing", "Water leakage, washrooms, taps, and drainage", "droplet"));
        categoryRepository.save(new Category("Furniture", "Broken chairs, damaged desks, boards, podiums", "armchair"));
        categoryRepository.save(new Category("Cleaning", "Sanitation, waste disposal, corridor cleaning", "sparkles"));
        categoryRepository.save(new Category("Equipment", "Air conditioners, water dispensers, audio systems", "tool"));
        categoryRepository.save(new Category("Infrastructure", "Door locks, window glass, tile repairs, ceiling cracks", "building"));

        // 3. Campus Locations
        locationRepository.save(new Location("Block A", "1st Floor", "Room 101", "Main Lecture Hall"));
        locationRepository.save(new Location("Block A", "1st Floor", "Washroom", "Faculty and Student Washroom"));
        locationRepository.save(new Location("Block A", "2nd Floor", "Room 201", "Classroom"));
        locationRepository.save(new Location("Block B", "3rd Floor", "Room 304", "Smart Classroom with A/V system"));
        locationRepository.save(new Location("Block B", "3rd Floor", "Lab 301", "Computer Science Programming Lab"));
        locationRepository.save(new Location("Library Building", "2nd Floor", "Reading Hall", "Central Library Silent Zone"));
        locationRepository.save(new Location("Campus Center", "Ground Floor", "Cafeteria", "Student Food Court Area"));

        System.out.println("✅ Master accounts and campus categories seeded. Zero fake complaints created.");
    }
}
