 classs-app


---
NB.The branch is in MASTER

Class Scheduling App for EiTM (EiT-M)**
Project Status: In Progress 


This project is a Class Scheduling App developed for the EiTM (School of Computing) Department at the Ethiopian Institute of Technology-Mekelle (EiT-M). It automates class scheduling, ensuring conflict-free timetables for students, instructors, and department heads.

This app is still under development and will be completed before the deadline. However, some functionalities might not be fully implemented or may not work as expected.  

---

 Features & Functionalities
the algorthims :
to be Implemented
- User Roles & Authentication** → Department heads, instructors, and students have different access levels.
- Course Scheduling** → Assigns courses to time slots while avoiding conflicts.
- Instructor Assignment** → Ensures no instructor is double-booked.
- Firebase Integration** → Stores and updates schedules in real time.
- Student Dashboard** → Displays the class schedule for students.

 Work in Progress
- Conflict Resolution System*→ Detects and fixes scheduling conflicts automatically.
- Manual Adjustments → Department heads can modify schedules.
- Instructor Preferences → Assigns classes based on availability.
- **UI/UX Enhancements** → Improving app usability and experience.

---

 How the Scheduling Algorithm Works
*Step 1: Collect Required Data
- List of courses in the WiTM department.
- Sections (e.g., "3rd Year Computer Science").
- Available time slots.
- List of instructors and their assigned courses.

Step 2: Assign Courses to Time Slots
1. Pick an available time slot.
2. Check if another class is already scheduled for the same section.
3. If no conflict → Assign the course.
4. If a conflict exists → Try another available time slot.

Step 3: Assign Instructors**
- Ensure no instructor is double-booked.
- Adjust schedules when conflicts arise.
- Validate and finalize the schedule.

*Step 4: Store & Display the Final Schedule
- Students → See only their section’s schedule.
- Instructors → View only their assigned courses.
- Department Heads → Edit and manage schedules.

---
* Known Issues & Limitations
- Some functionalities may not work properly yet.
- Automated conflict resolution is still under testing.
- UI/UX improvements are ongoing.
- The database structure may change as new features are added.
- and other things might not require.

---

 Future Improvements
- Optimize scheduling logic** for better efficiency.
- Add AI-based scheduling enhancements .
- Enable real-time notifications for schedule updates.
- Allow instructor and student feedback  for fairness.

---

Contact Information
Developer: Bethelhem Molla  
Email:betimuler16@gmail.com  



