const fs = require('fs');

// Function to convert a number string from any base to decimal
function convertToDecimal(valueStr, baseStr) {
    const base = parseInt(baseStr, 10);
    // Handle large numbers with BigInt
    return BigInt(parseInt(valueStr, base)).toString();
}

// Function to calculate the constant term using Lagrange interpolation
function findConstantTerm(points, k) {
    // We only need the first k points since k = m+1 is sufficient
    const selectedPoints = points.slice(0, k);
    const m = k - 1;
    let secret = BigInt(0);
    
    for (let i = 0; i < selectedPoints.length; i++) {
        const xi = BigInt(selectedPoints[i].x);
        const yi = BigInt(selectedPoints[i].y);
        
        let numerator = BigInt(1);
        let denominator = BigInt(1);
        
        for (let j = 0; j < selectedPoints.length; j++) {
            if (i === j) continue;
            const xj = BigInt(selectedPoints[j].x);
            numerator *= (-xj);
            denominator *= (xi - xj);
        }
        
        const term = (yi * numerator) / denominator;
        secret += term;
    }
    
    return secret.toString();
}

// Main function to process the input
function processInput(filename) {
    const rawData = fs.readFileSync(filename);
    const data = JSON.parse(rawData);
    
    const n = data.keys.n;
    const k = data.keys.k;
    
    const points = [];
    
    for (let key in data) {
        if (key === 'keys') continue;
        
        const x = parseInt(key, 10);
        const base = data[key].base;
        const value = data[key].value;
        
        // Convert y from given base to decimal
        let y;
        try {
            y = parseInt(value, parseInt(base, 10)).toString();
        } catch (e) {
            // For very large numbers that might exceed Number limits, use BigInt
            y = BigInt(parseInt(value, parseInt(base, 10))).toString();
        }
        
        points.push({ x, y });
    }
    
    // Sort points by x value to ensure consistent ordering
    points.sort((a, b) => a.x - b.x);
    
    const secret = findConstantTerm(points, k);
    console.log(`Secret for ${filename}: ${secret}`);
}

// Process both test cases
processInput('testcase1.json');
processInput('testcase2.json');