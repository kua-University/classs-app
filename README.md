

# Class Scheduling App for EiT-M

**Project Status:** done

This app is designed to automate class scheduling for the School of Computing at Ethiopian Institute of Technology-Mekelle (EiT-M). It ensures conflict-free timetables for students, instructors, and department heads, with all data stored and updated in real-time via Firebase.



## Features & Functionalities

- **User Roles & Authentication:** Department heads, instructors, and students have distinct access levels.
- **Course Scheduling:** Automatically assigns courses to time slots while avoiding conflicts.
- **Instructor Assignment:** Ensures no instructor is double-booked.
- **Firebase Integration:** Real-time updates for schedules stored in Firebase.
- **Admin Management:** Admin features for user management, including role assignments and access control.



## How It Works

### Step 1: Collect Data
- List of courses, sections, instructors, and available time slots.

### Step 2: Assign Courses
- Courses are automatically assigned to time slots.
- Conflicts are resolved by checking for overlaps with other classes.

### Step 3: Assign Instructors
- Ensures that instructors are not double-booked.
  
### Step 4: Admin System
- Department heads can manage users and schedules through an admin panel.

### Step 5: Display Schedules
- Students see only their assigned courses.
- Instructors see only their teaching assignments.
- Department heads can manage and adjust schedules.



## Known Issues

- Some functionalities may not be fully implemented.
- The conflict resolution system is still under testing.



## Future Improvements

- Enhanced AI scheduling for better efficiency.
- Real-time notifications for updates.



For more details, contact the developer at: **betimuler16@gmail.com**
