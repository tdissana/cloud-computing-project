CREATE SCHEMA IF NOT EXISTS salary;

DROP TABLE IF EXISTS salary.approved_salaries;

CREATE TABLE salary.approved_salaries (
      id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
      company_name VARCHAR(255),
      job_title VARCHAR(255),
      seniority_level VARCHAR(100),
      employment_type VARCHAR(100),
      country VARCHAR(100),
      city VARCHAR(100),
      gross_monthly_salary DECIMAL(15, 2),
      currency VARCHAR(10),
      additional_compensation DECIMAL(15, 2),
      years_of_experience INT,
      years_at_company INT,
      tech_stack VARCHAR(500),
      anonymized BOOLEAN DEFAULT FALSE,
      approved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      upvotes INT DEFAULT 0,
      downvotes INT DEFAULT 0
);