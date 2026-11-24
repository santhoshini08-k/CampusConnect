import javax.swing.*;
import java.awt.*;
import java.util.*;

public class CampusConnect {

    static ArrayList<Event> events = new ArrayList<>();
    static ArrayList<Student> students = new ArrayList<>();
    static ArrayList<String> registrations = new ArrayList<>();

    static JFrame frame;
    static Student currentStudent = null;

    static class Event {
        int id;
        String title, description, date, venue;
        boolean active;

        Event(int id, String title, String description,
              String date, String venue) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.date = date;
            this.venue = venue;
            this.active = true;
        }
    }

    static class Student {
        String username, password;

        Student(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    static void setup() {
        frame = new JFrame("CampusConnect");
        frame.setSize(950, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    static JButton button(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        return b;
    }

    static void home() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(25, 55, 100));

        JLabel heading = new JLabel("  CampusConnect");
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Arial", Font.BOLD, 26));

        JButton admin = button("Admin Login");
        top.add(heading, BorderLayout.WEST);
        top.add(admin, BorderLayout.EAST);

        admin.addActionListener(e -> adminLogin());

        frame.add(top, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("ACTIVE EVENTS");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        boolean found = false;

        for (Event event : events) {
            if (event.active) {
                found = true;

                JPanel card = new JPanel(new BorderLayout(15, 10));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));

                JLabel name = new JLabel(event.title);
                name.setFont(new Font("Arial", Font.BOLD, 20));

                JLabel info = new JLabel(
                    "Date: " + event.date +
                    "     Venue: " + event.venue
                );

                JButton view = button("View Details");

                JPanel details = new JPanel();
                details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
                details.add(name);
                details.add(Box.createVerticalStrut(8));
                details.add(info);

                card.add(details, BorderLayout.CENTER);
                card.add(view, BorderLayout.EAST);

                view.addActionListener(e -> eventDetails(event));

                content.add(card);
                content.add(Box.createVerticalStrut(15));
            }
        }

        if (!found) {
            content.add(new JLabel("No active events available."));
        }

        frame.add(new JScrollPane(content), BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    static void eventDetails(Event event) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel(event.title);
        title.setFont(new Font("Arial", Font.BOLD, 25));

        left.add(title);
        left.add(Box.createVerticalStrut(20));
        left.add(new JLabel("Date: " + event.date));
        left.add(Box.createVerticalStrut(10));
        left.add(new JLabel("Venue: " + event.venue));
        left.add(Box.createVerticalStrut(20));
        left.add(new JLabel("Description:"));
        left.add(new JLabel("<html><body style='width:350px'>"
                + event.description + "</body></html>"));

        JButton ok = button("OK");
        left.add(Box.createVerticalStrut(30));
        left.add(ok);

        JPanel right = new JPanel();
        right.setBorder(BorderFactory.createEmptyBorder(60, 20, 20, 20));

        JButton register = button("Register for This Event");
        right.add(register);

        ok.addActionListener(e -> home());
        register.addActionListener(e -> studentLogin(event));

        frame.add(left, BorderLayout.CENTER);
        frame.add(right, BorderLayout.EAST);

        frame.revalidate();
        frame.repaint();
    }

    static void studentLogin(Event event) {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        Object[] fields = {
            "Username:", username,
            "Password:", password
        };

        int result = JOptionPane.showConfirmDialog(
            frame, fields, "Student Login",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        for (Student s : students) {
            if (s.username.equals(username.getText()) &&
                s.password.equals(new String(password.getPassword()))) {
                currentStudent = s;
                registerStudent(event);
                return;
            }
        }

        int choice = JOptionPane.showConfirmDialog(
            frame,
            "Account not found. Do you want to Sign Up?",
            "Sign Up",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            studentSignup(event);
        }
    }

    static void studentSignup(Event event) {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        Object[] fields = {
            "Create Username:", username,
            "Create Password:", password
        };

        int result = JOptionPane.showConfirmDialog(
            frame, fields, "Student Sign Up",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        String user = username.getText().trim();
        String pass = new String(password.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Fill all fields.");
            return;
        }

        for (Student s : students) {
            if (s.username.equals(user)) {
                JOptionPane.showMessageDialog(
                    frame, "Username already exists."
                );
                return;
            }
        }

        currentStudent = new Student(user, pass);
        students.add(currentStudent);

        JOptionPane.showMessageDialog(frame, "Account created!");
        registerStudent(event);
    }

    static void registerStudent(Event event) {
        String key = currentStudent.username + "-" + event.id;

        if (registrations.contains(key)) {
            JOptionPane.showMessageDialog(
                frame, "You are already registered for this event."
            );
            return;
        }

        registrations.add(key);

        JOptionPane.showMessageDialog(
            frame,
            "Registration Successful!\nStudent: "
            + currentStudent.username +
            "\nEvent: " + event.title
        );

        home();
    }

    static void adminLogin() {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        Object[] fields = {
            "Admin Username:", username,
            "Admin Password:", password
        };

        int result = JOptionPane.showConfirmDialog(
            frame, fields, "Admin Login",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            if (username.getText().equals("admin") &&
                new String(password.getPassword()).equals("admin123")) {
                adminDashboard();
            } else {
                JOptionPane.showMessageDialog(
                    frame, "Invalid Admin Login"
                );
            }
        }
    }

    static void adminDashboard() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JLabel heading = new JLabel("ADMIN DASHBOARD");
        heading.setFont(new Font("Arial", Font.BOLD, 26));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panel = new JPanel(new GridLayout(5, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        JButton add = button("Add Event");
        JButton view = button("View All Events");
        JButton toggle = button("Activate / Deactivate Events");
        JButton registrationsBtn = button("View Student Registrations");
        JButton logout = button("Logout");

        panel.add(add);
        panel.add(view);
        panel.add(toggle);
        panel.add(registrationsBtn);
        panel.add(logout);

        add.addActionListener(e -> addEvent());
        view.addActionListener(e -> viewAllEvents());
        toggle.addActionListener(e -> toggleEvent());
        registrationsBtn.addActionListener(e -> viewRegistrations());
        logout.addActionListener(e -> home());

        frame.add(heading, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    static void addEvent() {
        JTextField title = new JTextField();
        JTextField description = new JTextField();
        JTextField date = new JTextField();
        JTextField venue = new JTextField();

        Object[] fields = {
            "Event Name:", title,
            "Description:", description,
            "Date:", date,
            "Venue:", venue
        };

        int result = JOptionPane.showConfirmDialog(
            frame, fields, "Add New Event",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            if (title.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Enter event name.");
                return;
            }

            events.add(new Event(
                events.size() + 1,
                title.getText(),
                description.getText(),
                date.getText(),
                venue.getText()
            ));

            JOptionPane.showMessageDialog(frame, "Event Added Successfully!");
            adminDashboard();
        }
    }

    static void viewAllEvents() {
        StringBuilder data = new StringBuilder();

        for (Event event : events) {
            data.append(event.id)
                .append(". ")
                .append(event.title)
                .append(" | ")
                .append(event.active ? "ACTIVE" : "INACTIVE")
                .append("\n");
        }

        JOptionPane.showMessageDialog(
            frame,
            data.length() == 0 ? "No events found." : data.toString(),
            "All Events",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    static void toggleEvent() {
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No events available.");
            return;
        }

        String[] choices = events.stream()
            .map(e -> e.id + " - " + e.title +
                (e.active ? " (Active)" : " (Inactive)"))
            .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
            frame, "Select Event:", "Event Status",
            JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]
        );

        if (selected != null) {
            int id = Integer.parseInt(selected.split(" - ")[0]);

            for (Event event : events) {
                if (event.id == id) {
                    event.active = !event.active;
                    break;
                }
            }

            JOptionPane.showMessageDialog(frame, "Event status updated.");
            adminDashboard();
        }
    }

    static void viewRegistrations() {
        StringBuilder data = new StringBuilder();

        for (String registration : registrations) {
            data.append(registration.replace("-", " -> "))
                .append("\n");
        }

        JOptionPane.showMessageDialog(
            frame,
            data.length() == 0 ? "No registrations yet." : data.toString(),
            "Student Registrations",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setup();
            home();
        });
    }
}