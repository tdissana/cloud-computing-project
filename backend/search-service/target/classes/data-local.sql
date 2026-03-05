INSERT INTO salary.approved_salaries
(company_name, job_title, seniority_level, employment_type, country, city, gross_monthly_salary, currency, years_of_experience, tech_stack, anonymized, upvotes, downvotes)
VALUES
    ('WSO2',        'Software Engineer',        'Mid',    'Full-time', 'Sri Lanka', 'Colombo', 150000, 'LKR', 3, 'Java, Spring Boot, Kubernetes', false, 10, 1),
    ('Virtusa',     'Senior DevOps Engineer',   'Senior', 'Full-time', 'Sri Lanka', 'Colombo', 220000, 'LKR', 6, 'AWS, Docker, Terraform',        false,  8, 0),
    ('IFS',         'QA Engineer',              'Junior', 'Full-time', 'Sri Lanka', 'Colombo',  90000, 'LKR', 1, 'Selenium, Java',                false,  5, 2),
    ('99x',         'Full Stack Developer',     'Mid',    'Full-time', 'Sri Lanka', 'Colombo', 175000, 'LKR', 4, 'React, Node.js, PostgreSQL',    false, 12, 0),
    ('Sysco LABS',  'Data Engineer',            'Senior', 'Full-time', 'Sri Lanka', 'Colombo', 280000, 'LKR', 7, 'Python, Spark, Kafka',          false, 15, 1),
    ('Anonymous',   'Backend Engineer',         'Mid',    'Contract',  'Sri Lanka', 'Kandy',   160000, 'LKR', 4, 'Go, Docker',                    true,   3, 0),
    ('Wiley',       'Software Engineer',        'Junior', 'Full-time', 'Sri Lanka', 'Colombo', 110000, 'LKR', 2, 'React, TypeScript',             false,  6, 1),
    ('Cambio',      'Lead Software Engineer',   'Lead',   'Full-time', 'Sri Lanka', 'Colombo', 350000, 'LKR', 9, 'Java, Microservices, AWS',      false, 20, 0);