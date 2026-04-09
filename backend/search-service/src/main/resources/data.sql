-- =========================================================
-- Salary Submission Seed Data
-- =========================================================

INSERT INTO salary.submission
(company_name, job_title, experience_level, seniority, country, currency, total_compensation, base_salary, skills, anonymize, status)
VALUES
    -- Approved — Sri Lanka LKR
    ('WSO2',       'Software Engineer',      'Mid',    3, 'Sri Lanka', 'LKR', 175000, 150000, 'Java, Spring Boot, Kubernetes', false, 'APPROVED'),
    ('Virtusa',    'Senior DevOps Engineer', 'Senior', 6, 'Sri Lanka', 'LKR', 240000, 220000, 'AWS, Docker, Terraform',        false, 'APPROVED'),
    ('IFS',        'QA Engineer',            'Junior', 1, 'Sri Lanka', 'LKR', 100000,  90000, 'Selenium, Java',                false, 'APPROVED'),
    ('99x',        'Full Stack Developer',   'Mid',    4, 'Sri Lanka', 'LKR', 200000, 175000, 'React, Node.js, PostgreSQL',    false, 'APPROVED'),
    ('Sysco LABS', 'Data Engineer',          'Senior', 7, 'Sri Lanka', 'LKR', 310000, 280000, 'Python, Spark, Kafka',          false, 'APPROVED'),
    (NULL,         'Backend Engineer',       'Mid',    4, 'Sri Lanka', 'LKR', 180000, 160000, 'Go, Docker',                    true,  'APPROVED'),
    ('Wiley',      'Software Engineer',      'Junior', 2, 'Sri Lanka', 'LKR', 125000, 110000, 'React, TypeScript',             false, 'APPROVED'),
    ('Cambio',     'Lead Software Engineer', 'Lead',   9, 'Sri Lanka', 'LKR', 390000, 350000, 'Java, Microservices, AWS',      false, 'APPROVED'),

    -- Sri Lanka but foreign currency
    ('MillenniumIT', 'Software Engineer',    'Mid',    4, 'Sri Lanka', 'GBP', 42000,  38000, 'C++, Trading Systems',           false, 'APPROVED'),
    ('Zone24x7',     'Data Scientist',       'Senior', 6, 'Sri Lanka', 'EUR', 58000,  52000, 'Python, ML, TensorFlow',         false, 'APPROVED'),
    (NULL,           'Contract Developer',   'Mid',    3, 'Sri Lanka', 'GBP', 42000,  38000, 'C#, Azure',                      true,  'APPROVED'),

    -- Outside Sri Lanka
    ('Grab',       'Mobile Engineer',        'Senior', 6, 'Singapore', 'SGD', 132000, 120000, 'Kotlin, Swift',                 false, 'APPROVED'),
    ('Infosys',    'Systems Engineer',       'Mid',    4, 'India',     'INR', 1800000, 1600000, 'Java, Spring',                 false, 'APPROVED'),
    ('TCS',        'Consultant',             'Junior', 2, 'India',     'INR',  850000,  780000, 'SQL, PL/SQL',                 false, 'APPROVED'),
    ('Monzo',      'Backend Engineer',       'Mid',    5, 'United Kingdom', 'GBP', 72000, 65000, 'Go, Kubernetes',             false, 'APPROVED'),
    ('Stripe',     'Product Engineer',       'Senior', 7, 'Ireland',   'EUR', 98000,  88000, 'Ruby, Payments',                 false, 'APPROVED'),
    ('Amazon',     'SDE II',                 'Mid',    5, 'United States', 'USD', 165000, 140000, 'Java, AWS',                 false, 'APPROVED'),
    ('Google',     'L5 Engineer',            'Senior', 8, 'United States', 'USD', 245000, 200000, 'Python, GCP',               false, 'APPROVED'),
    ('Careem',     'Android Developer',      'Mid',    4, 'United Arab Emirates', 'AED', 288000, 260000, 'Kotlin, Android',   false, 'APPROVED'),
    ('Atlassian',  'Frontend Engineer',      'Senior', 6, 'Australia', 'AUD', 165000, 150000, 'React, TypeScript',            false, 'APPROVED'),

    -- Pending
    ('Pending Corp', 'Intern',               'Junior', 0, 'Sri Lanka', 'LKR',  35000,  32000, 'HTML, CSS',                    false, 'PENDING'),
    (NULL,           'Mystery Role',         'Mid',    3, 'India',     'INR', 1200000, 1100000, 'Java',                       true,  'PENDING'),
    ('QueueSoft',    'QA Engineer',          'Junior', 1, 'United Kingdom', 'GBP', 32000, 30000, 'Manual testing',           false, 'PENDING'),

    -- Rejected
    ('OldCo',        'Legacy Developer',     'Senior', 6, 'Sri Lanka', 'LKR', 200000, 180000, 'COBOL',                        false, 'REJECTED'),
    ('Spam Inc',     'Fake Title',           'Lead',   9, 'Singapore', 'SGD', 9999999, 9000000, 'N/A',                        false, 'REJECTED');

-- =========================================================
-- Create vote rows for ALL submissions (approved + pending + rejected)
-- =========================================================

INSERT INTO vote.voteresults (id, up_vote_count, down_vote_count)
SELECT CAST(id AS VARCHAR), 0, 0
FROM salary.submission;

-- =========================================================
-- Normal vote activity
-- =========================================================

UPDATE vote.voteresults SET up_vote_count = 12,  down_vote_count = 1 WHERE id = '1';
UPDATE vote.voteresults SET up_vote_count = 8,   down_vote_count = 0 WHERE id = '2';
UPDATE vote.voteresults SET up_vote_count = 3,   down_vote_count = 2 WHERE id = '3';
UPDATE vote.voteresults SET up_vote_count = 150000,  down_vote_count = 400 WHERE id = '4';

-- =========================================================
-- High vote counts for stress testing (across different statuses)
-- =========================================================

-- Approved entries
UPDATE vote.voteresults SET up_vote_count = 150000,  down_vote_count = 120   WHERE id = '6';
UPDATE vote.voteresults SET up_vote_count = 275000,  down_vote_count = 2000  WHERE id = '9';

-- Pending entries
UPDATE vote.voteresults SET up_vote_count = 180000,  down_vote_count = 500   WHERE id = '21';
UPDATE vote.voteresults SET up_vote_count = 990000,  down_vote_count = 12000 WHERE id = '22';

-- Rejected entries
UPDATE vote.voteresults SET up_vote_count = 100,  down_vote_count = 4500  WHERE id = '24';
UPDATE vote.voteresults SET up_vote_count = 10, down_vote_count = 20 WHERE id = '25';