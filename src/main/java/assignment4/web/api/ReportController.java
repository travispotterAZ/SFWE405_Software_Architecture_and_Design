package assignment4.web.api;

import org.springframework.web.bind.annotation.RestController; //Spring Web
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import assignment4.service.ReportService;


@RestController
@RequestMapping("/reports")       //base URL path for endpoints of ReportController
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/students/age")
    public String reportStudentsByAge() {    //Endpoint for report of students grouped by age
        return reportService.reportStudentsByAge();
    }
}
