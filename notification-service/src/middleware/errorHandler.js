const errorHandler = (err, req, res, next) => {
    console.error('❌ Error:', err.message);

    // Mongoose validation error
    if (err.name === 'ValidationError') {
        const messages = Object.values(err.errors).map(e => e.message);
        return res.status(400).json({
            error: 'Validation Failed',
            details: messages
        });
    }

    // Mongoose bad ObjectId (e.g. invalid MongoDB ID format)
    if (err.name === 'CastError') {
        return res.status(400).json({
            error: 'Invalid ID format',
            message: `Invalid value for field: ${err.path}`
        });
    }

    // Mongoose duplicate key error
    if (err.code === 11000) {
        return res.status(409).json({
            error: 'Duplicate Entry',
            message: 'A record with this value already exists'
        });
    }

    // Default internal server error
    res.status(500).json({
        error: 'Internal Server Error',
        message: err.message || 'Something went wrong'
    });
};

module.exports = errorHandler;
