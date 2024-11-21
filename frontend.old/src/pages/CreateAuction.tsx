import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const CreateAuction: React.FC = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        type: 'DUTCH',
        auctionOwnerId: 1,
        initialPrice: 0,
        startTime: '',
        minimumPrice: 0,
        duration: 60
    });
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        try {
            const response = await fetch('http://localhost:8083/auction', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    ...formData,
                    initialPrice: Number(formData.initialPrice),
                    minimumPrice: Number(formData.minimumPrice),
                    duration: Number(formData.duration),
                    startTime: new Date(formData.startTime).toISOString()
                })
            });

            if (!response.ok) {
                throw new Error('Failed to create auction');
            }

            navigate('/auction-list');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Error creating auction');
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        setFormData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    return (
        <div className="create-auction-container">
            <h1>Create New Auction</h1>
            {error && <div className="error-message">{error}</div>}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Name:</label>
                    <input 
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />
                </div>
                
                <div className="form-group">
                    <label>Description:</label>
                    <textarea
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Type:</label>
                    <select name="type" value={formData.type} onChange={handleChange}>
                        <option value="DUTCH">Dutch Auction</option>
                        <option value="FORWARD">Forward Auction</option>
                    </select>
                </div>

                <div className="form-group">
                    <label>Initial Price:</label>
                    <input
                        type="number"
                        name="initialPrice"
                        value={formData.initialPrice}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Start Time:</label>
                    <input
                        type="datetime-local"
                        name="startTime"
                        value={formData.startTime}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Minimum Price:</label>
                    <input
                        type="number"
                        name="minimumPrice"
                        value={formData.minimumPrice}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Duration (minutes):</label>
                    <input
                        type="number"
                        name="duration"
                        value={formData.duration}
                        onChange={handleChange}
                        required
                    />
                </div>

                <button type="submit">Create Auction</button>
            </form>
        </div>
    );
};

export default CreateAuction;