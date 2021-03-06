package api;

import org.hibernate.Session;

import javax.persistence.*;
import java.util.*;

@Entity
public class Course {


    @Column
    private String name;

    @Column
    @Id
    private String courseCode;

    @ElementCollection
    @CollectionTable
    @Column
    private List<Department> departments;

    @ElementCollection
    @CollectionTable
    @Column
    private List<Integer> courseNumber;

    @Column(length = 10000)
    private String postConditions;


    @Column(length = 10000)
    private String preConditions;

    @Column
    private int credits;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Faculty_Project",
            joinColumns = {@JoinColumn(name = "courseCode")},
            inverseJoinColumns = {@JoinColumn(name = "email")}
    )
    private Set<Faculty> faculties = new HashSet<>();


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "Students_RegisteredCourse",
            joinColumns = {@JoinColumn(name = "courseCode")},
            inverseJoinColumns = {@JoinColumn(name = "email")}
    )
    private Set<Student> registeredStudents = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "Students_AuditedCourse",
            joinColumns = {@JoinColumn(name = "courseCode")},
            inverseJoinColumns = {@JoinColumn(name = "email")}
    )
    private Set<Student> auditedStudents = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Students_ShoppingCourse",
            joinColumns = {@JoinColumn(name = "courseCode")},
            inverseJoinColumns = {@JoinColumn(name = "email")}
    )
    private Set<Student> shoppingStudents = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<CourseEvent> courseEvents = new HashSet<>();


    public Course(String name, String courseCode, List<Department> departments, List<Integer> courseNumber, Set<Faculty> faculties, String postConditions, int credits) {
        this.name = name;
        this.courseCode = courseCode;
        this.departments = departments;
        this.courseNumber = courseNumber;
        this.faculties = faculties;
        this.postConditions = postConditions;
        this.credits = credits;
    }

    /**
     * Default constructor required for Hibernate
     */
    public Course() {
    }

    /**
     * It will break the search term using the space as a delimiter.
     *
     * @param searchTerm string consisting of keywords
     * @return List of Course whose precondition contains *any of the word* in search term
     */
    public static List<Course> search(String searchTerm) {
        List<Course> listOfAllCourses = new ArrayList<>();
        if (searchTerm == null) {
            return listOfAllCourses;
        }
        String sp[] = searchTerm.split(" ");

        Arrays.stream(sp).parallel().forEach(str -> {
            listOfAllCourses.addAll(searchByKeyword(str));
        });

        return listOfAllCourses;
    }

    /**
     * @param searchTerm search for <b>exact</b> occurrence of searchTerm in postCondition
     * @return List of Course whose precondition contains <b>exactly</b> the search term
     */
    public static List<Course> searchByKeyword(String searchTerm) {

        Session session = MySession.getSession();
        Query query = session.createQuery("FROM Course course where course.postConditions like :postCondition");
        query.setParameter("postCondition", "%" + searchTerm + "%");
        List<Course> list = query.getResultList();
        return list;

    }

    /**
     * If you want to list of all the Courses in the database
     *
     * @return List of all the Courses in the database
     */
    public static List<Course> getAllCourses() {
        Session session = MySession.getSession();
        Query query = session.createQuery("FROM Course");
        List<Course> list = query.getResultList();
        return list;
    }

    /**
     * Returns the course object with all of it's features with the given name
     * @param name The name of the course, like "Advanced Programming"
     * @return the course object corresponding to the name
     */
    public static Course getCourseByName(String name) {
        Session session = MySession.getSession();
        Query query = session.createQuery("FROM Course course where course.name = :nameC");
        query.setParameter("nameC", name);
        Course courseX = (Course) query.getResultList().get(0);
        return courseX;
    }

    /**
     * For testing
     * @param args
     */
    public static void main(String[] args) {
        Session session = MySession.getSession();
//        List<Course> course = Course.search("Students");
//        course.forEach(o -> {
//            o.getFaculties().forEach(
//                    faculty -> {
//                        System.out.println(faculty.getName());
//                    }
//            );
//        });


        Course course = Course.getCourseByName("Advanced Programming");
        course.getFaculties().stream().forEach(o -> {
            System.out.println(o.getEmail());
        });


        Student student = session.get(Student.class, "siddharth16268@iiitd.ac.in");
        student.insertAuditedCourse(course);
    }

    /**
     * @return list of events that are this week
     */
    public List<Event> getThisWeekEvents() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.getWeekYear(), calendar.getWeeksInWeekYear(), 0);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar.getWeekYear(), calendar.getWeeksInWeekYear(), 6);
        return getEventInTimeSpan(calendar.getTime(), calendar2.getTime());
    }

    /**
     * Returns all the events that occur between the date start and the date end
     *
     * @param start inclusive starting date
     * @param end   inclusive ending date
     * @return List of Event b/w start and end
     */
    public List<Event> getEventInTimeSpan(Date start, Date end) {
        Session session = MySession.getSession();
        Query query = session.createQuery("FROM Event event where event.course = :nocourse and ( event.date >= :startDate and event.date <= :endDate )", Event.class);
        query.setParameter("nocourse", this);
        query.setParameter("startDate", start);
        query.setParameter("endDate", end);
        List<Event> list = query.getResultList();
        return list;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreConditions() {
        return preConditions;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<Integer> getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(List<Integer> courseNumber) {
        this.courseNumber = courseNumber;
    }

    public Set<Faculty> getFaculties() {
        return faculties;
    }

    public void setFaculties(Set<Faculty> faculties) {
        this.faculties = faculties;
    }

    public void setPreConditions(String preConditions) {
        this.preConditions = preConditions;
    }

    public String getPostConditions() {
        return postConditions;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setPostConditions(String postConditions) {
        this.postConditions = postConditions;
    }

    public Set<Student> getRegisteredStudents() {
        return registeredStudents;
    }

    public void setRegisteredStudents(Set<Student> registeredStudents) {
        this.registeredStudents = registeredStudents;
    }

    public Set<Student> getAuditedStudents() {
        return auditedStudents;
    }

    public void setAuditedStudents(Set<Student> auditedStudents) {
        this.auditedStudents = auditedStudents;
    }

    public Set<Student> getShoppingStudents() {
        return shoppingStudents;
    }

    public void setShoppingStudents(Set<Student> shoppingStudents) {
        this.shoppingStudents = shoppingStudents;
    }

    public Set<CourseEvent> getCourseEvents() {
        return courseEvents;
    }

    public void setCourseEvents(Set<CourseEvent> courseEvents) {
        this.courseEvents = courseEvents;
    }

    @Override
    public boolean equals(Object o) {
        return (this.courseCode.equals(((Course) o).courseCode));
    }
}
