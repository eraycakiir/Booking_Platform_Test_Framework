pipeline {
    agent any
    
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Select browser for test execution'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['test', 'staging', 'dev'],
            description: 'Select environment for test execution'
        )
        choice(
            name: 'TEST_SUITE',
            choices: ['smoke', 'regression', 'api', 'all'],
            description: 'Select test suite to run'
        )
        booleanParam(
            name: 'PARALLEL_EXECUTION',
            defaultValue: false,
            description: 'Enable parallel test execution'
        )
        booleanParam(
            name: 'REMOTE_EXECUTION',
            defaultValue: true,
            description: 'Use Selenium Grid for remote execution'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '2',
            description: 'Number of parallel threads (if parallel execution is enabled)'
        )
    }
    
    environment {
        MAVEN_OPTS = '-Xmx1024m'
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk'
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "🚀 Starting Selenium Test Pipeline"
                    echo "Browser: ${params.BROWSER}"
                    echo "Environment: ${params.ENVIRONMENT}"
                    echo "Test Suite: ${params.TEST_SUITE}"
                    echo "Parallel Execution: ${params.PARALLEL_EXECUTION}"
                    echo "Remote Execution: ${params.REMOTE_EXECUTION}"
                }
                
                // Cleanup workspace
                cleanWs()
                
                // Checkout code
                checkout scm
                
                // Display Git information
                sh '''
                    echo "📋 Git Information:"
                    echo "Branch: $(git rev-parse --abbrev-ref HEAD)"
                    echo "Commit: $(git rev-parse --short HEAD)"
                    echo "Author: $(git log -1 --pretty=format:'%an <%ae>')"
                    echo "Message: $(git log -1 --pretty=format:'%s')"
                '''
            }
        }
        
        stage('Setup Environment') {
            steps {
                echo "🔧 Setting up test environment..."
                
                // Create reports directory
                sh 'mkdir -p reports/allure-results reports/allure-report reports/screenshots'
                
                // Set system properties
                script {
                    env.BROWSER = params.BROWSER
                    env.ENVIRONMENT = params.ENVIRONMENT
                    env.REMOTE_EXECUTION = params.REMOTE_EXECUTION
                    env.PARALLEL_EXECUTION = params.PARALLEL_EXECUTION
                    env.THREAD_COUNT = params.THREAD_COUNT
                }
                
                // Display Java and Maven versions
                sh '''
                    echo "☕ Java Version:"
                    java -version
                    echo "🔨 Maven Version:"
                    mvn -version
                '''
            }
        }
        
        stage('Start Selenium Grid') {
            when {
                expression { params.REMOTE_EXECUTION == true }
            }
            steps {
                echo "🌐 Starting Selenium Grid..."
                
                script {
                    try {
                        // Stop any existing containers
                        sh 'docker-compose down || true'
                        
                        // Start Selenium Grid
                        sh 'docker-compose up -d selenium-hub chrome-node firefox-node edge-node'
                        
                        // Wait for grid to be ready
                        echo "⏳ Waiting for Selenium Grid to be ready..."
                        timeout(time: 3, unit: 'MINUTES') {
                            waitUntil {
                                script {
                                    def result = sh(
                                        script: 'curl -sSf http://localhost:4444/status || exit 1',
                                        returnStatus: true
                                    )
                                    return result == 0
                                }
                            }
                        }
                        
                        echo "✅ Selenium Grid is ready!"
                        sh 'curl -s http://localhost:4444/status | jq .'
                        
                    } catch (Exception e) {
                        error "❌ Failed to start Selenium Grid: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('Compile & Test') {
            steps {
                echo "🔨 Compiling and running tests..."
                
                script {
                    def mavenCommand = "mvn clean test"
                    
                    // Add system properties
                    mavenCommand += " -Dbrowser=${params.BROWSER}"
                    mavenCommand += " -Denvironment=${params.ENVIRONMENT}"
                    mavenCommand += " -Dremote.execution=${params.REMOTE_EXECUTION}"
                    
                    // Configure test suite
                    if (params.TEST_SUITE != 'all') {
                        mavenCommand += " -Dgroups=${params.TEST_SUITE}"
                    }
                    
                    // Configure parallel execution
                    if (params.PARALLEL_EXECUTION) {
                        mavenCommand += " -Dparallel=methods"
                        mavenCommand += " -DthreadCount=${params.THREAD_COUNT}"
                    }
                    
                    echo "🚀 Executing: ${mavenCommand}"
                    
                    // Run tests
                    sh "${mavenCommand}"
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                echo "📊 Generating test reports..."
                
                // Generate Allure report
                script {
                    try {
                        sh 'mvn allure:report'
                        echo "✅ Allure report generated successfully"
                    } catch (Exception e) {
                        echo "⚠️ Failed to generate Allure report: ${e.getMessage()}"
                    }
                }
                
                // Archive test results
                archiveArtifacts artifacts: 'reports/**/*', allowEmptyArchive: true
                
                // Publish test results
                publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                
                // Publish Allure report
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'reports/allure-results']]
                ])
            }
        }
        
        stage('Cleanup') {
            steps {
                echo "🧹 Cleaning up resources..."
                
                script {
                    if (params.REMOTE_EXECUTION) {
                        // Stop Selenium Grid
                        sh 'docker-compose down || true'
                        echo "🛑 Selenium Grid stopped"
                    }
                }
                
                // Clean up large files but keep reports
                sh '''
                    find . -name "*.log" -size +10M -delete || true
                    find target -name "*.jar" -delete || true
                '''
            }
        }
    }
    
    post {
        always {
            echo "📋 Pipeline completed"
            
            // Collect logs
            script {
                if (fileExists('reports/application.log')) {
                    archiveArtifacts artifacts: 'reports/application.log', allowEmptyArchive: true
                }
            }
        }
        
        success {
            echo "✅ Pipeline completed successfully!"
            
            // Send success notification (configure as needed)
            emailext(
                subject: "✅ Selenium Tests Passed - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    <h2>✅ Test Execution Successful</h2>
                    <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build:</strong> #${env.BUILD_NUMBER}</p>
                    <p><strong>Browser:</strong> ${params.BROWSER}</p>
                    <p><strong>Environment:</strong> ${params.ENVIRONMENT}</p>
                    <p><strong>Test Suite:</strong> ${params.TEST_SUITE}</p>
                    <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    <p><strong>Allure Report:</strong> <a href="${env.BUILD_URL}allure">View Report</a></p>
                """,
                mimeType: 'text/html',
                to: '${DEFAULT_RECIPIENTS}'
            )
        }
        
        failure {
            echo "❌ Pipeline failed!"
            
            // Capture additional debug information
            script {
                try {
                    sh 'docker logs selenium-hub || true'
                    sh 'docker ps -a || true'
                } catch (Exception e) {
                    echo "Failed to capture debug info: ${e.getMessage()}"
                }
            }
            
            // Send failure notification
            emailext(
                subject: "❌ Selenium Tests Failed - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    <h2>❌ Test Execution Failed</h2>
                    <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build:</strong> #${env.BUILD_NUMBER}</p>
                    <p><strong>Browser:</strong> ${params.BROWSER}</p>
                    <p><strong>Environment:</strong> ${params.ENVIRONMENT}</p>
                    <p><strong>Test Suite:</strong> ${params.TEST_SUITE}</p>
                    <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    <p><strong>Console Output:</strong> <a href="${env.BUILD_URL}console">View Logs</a></p>
                """,
                mimeType: 'text/html',
                to: '${DEFAULT_RECIPIENTS}'
            )
        }
        
        cleanup {
            // Final cleanup
            sh 'docker-compose down --remove-orphans || true'
            cleanWs()
        }
    }
}