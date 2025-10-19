package com.example.pockettru;

public class CourseModel
{
    private String CourseID;
    private String CourseName;
    private String description;

    public CourseModel(String CourseID, String CourseName, String description)
    {
        this.CourseID = CourseID;
        this.CourseName = CourseName;
        this.description = description;
    }
    public CourseModel()
    {
        this.CourseID = "";
        this.CourseName = "";
        this.description = "";
    }
    public String getCourseID()
    {
        return CourseID;
    }
    public String getCourseName()
    {
        return CourseName;
    }
    public String getDescription()
    {
        return description;
    }

    public void setCourseID(String courseID) {
        CourseID = courseID;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
